public class Tomcat优化{
	/**
	 * Tomcat是我们经常使用的 servlet容器之一，甚至很多线上产品都使用 Tomcat充当服务器。
	 * 而且优化后的Tomcat性能提升显著。
	 * tomcat默认参数是为开发环境制定，而非适合生产环境，尤其是内存和线程的配置，默认都很低，容易成为性能瓶颈。
	 */
	public void Tomcat优化(){
		cmd tomcat bin/ 运行catalina debug 观察启动情况
		// 一、java虚拟机内存优化
	  	 默认情况下Tomcat的相关内存配置较低，这对于一些大型项目显然是不够用的，
	  	 这些项目运行就已经耗费了大部分内存空间，何况大规模访问的情况。
	  	 即使是本文中的这个只有一个页面的超小项目，在并发达到一定程度后也会抛出以下类似异常：
	  	 严重: Exception invoking periodic operation: java.lang.OutOfMemoryError: Java heap space  
	  	 严重: Error processing request java.lang.OutOfMemoryError: GC overhead limit exceeded;
		说明Tomcat已经无力支持访问处理，内部GC也已经“无能无力”。
		所以一般情况下我们需要重新配置Tomcat的相关内存大小。

		//修改内存等 JVM相关配置：
		linux修改TOMCAT_HOME/bin/catalina.sh，在该文件的第一行加入：
		JAVA_OPTS="-XX:PermSize=64M -XX:MaxPermSize=128m -Xms512m -Xmx1024m -Duser.timezone=Asia/Shanghai"
		
		windows下修改TOMCAT_HOME/bin/catalina.bat，在第一行加入如下代码：
		set JAVA_OPTS=-server -Xms512m -Xmx1024m -XX:MaxNewSize=512m -XX:PermSize=256m -XX:MaxPermSize=256m 
		这里要注意，服务器内存空间是否足够大，最大堆内存是1024m，对于现在的硬件还是偏低，实施时，还是按照机器具体硬件配置优化。
		
		-server：启用 JDK的 server 版本；
		-Xms：Java虚拟机初始化时堆的最小内存，一般与 Xmx配置为相同值，这样的好处是GC不必再为扩展内存空间而消耗性能；
		-Xmx：Java虚拟机可使用堆的最大内存；
		-XX:PermSize：JVM初始分配的非堆内存；
		-XX:MaxPermSize：JVM最大允许分配的非堆内存，按需分配；
		-XX:MaxNewSize=512m JVM堆区域新生代内存的最大可分配大小(PermSize不属于堆区)
		
		//二、配置优化
		//1.Connector 优化
		Connector是连接器，负责接收客户的请求，以及向客户端回送响应的消息。
		所以 Connector的优化是重要部分。默认情况下 Tomcat只支持200线程访问，超过这个数量的连接将被等待甚至超时放弃，
		所以我们需要提高这方面的处理能力。
		修改这部分配置需要修改TOMCAT_HOME/conf/server.xml，打开server.xml找到Connector 标签项
		其中port代表服务接口；protocol代表协议类型；connectionTimeout代表连接超时时间，单位为毫秒；
		redirectPort代表安全通信（https）转发端口，一般配置成443。
		优化后的配置：
		<Connector port="80"   
	          protocol="org.apache.coyote.http11.Http11NioProtocol"
	          maxThreads="1000"   
	          minSpareThreads="100"  
	          maxSpareThreads="1000"
	          acceptCount="1000"  
	          maxConnections="1000"  
	          connectionTimeout="20000"   
	          maxHttpHeaderSize="8192"  
	          tcpNoDelay="true"  
	          compression="on"  
	          compressionMinSize="2048"  
	          disableUploadTimeout="true"  
	          redirectPort="8443"  
	      	  enableLookups="false"  
	          URIEncoding="UTF-8" /> 
	maxSpareThreads="500"///一旦创建的线程超过这个值，Tomcat就会关闭不再需要的socket线程。
	修改tomcat让其支持NIO: protocol="org.apache.coyote.http11.Http11NioProtocol"
	NIO：NIO(New I/O)，是Java SE 1.4及后续版本提供的一种新的I/O操作方式(即java.nio包及其子包)。
	Java nio是一个基于缓冲区、并能提供非阻塞I/O操作的Java API，
	因此nio也被看成是non-blocking I/O的缩写。它拥有比传统I/O操作(bio)更好的并发运行性能。
	默认配置：protocol="HTTP/1.1"
	可选项配置：protocol="org.apache.coyote.http11.Http11Nio2Protocol" 
	可选项配置：protocol="org.apache.coyote.http11.Http11AprProtocol" 
	//BIO  
	protocol="HTTP/1.1"  
	//NIO  
	protocol="org.apache.coyote.http11.Http11NioProtocol"  
	//NIO2  
	protocol="org.apache.coyote.http11.Http11Nio2Protocol"  
	//APR  
	protocol="org.apache.coyote.http11.Http11AprProtocol"  

	maxThreads：由该连接器创建的处理请求线程的最大数目，也就是可以处理的同时请求的最大数目。如果未配置默认值为200。 maxThreads是一个重要的配置属性，maxThreads配置的合理直接影响了Tomcat的相关性能，maxThreads并不是配置的越大越好，事实上你即使配置成999999也是没有用的，因为这个最大值是受操作系统及相关硬件所制约的，并且最大值并不一定是最优值，所以我们追寻的应该是最优值而不是最大值。
	minSpareThreads：线程的最小运行数目，这些始终保持运行。如果未指定，默认值为10。
	acceptCount：指定当所有可以使用的处理请求的线程数都被使用时，可以放到处理队列中的请求数，超过这个数的请求将不予处理
	maxConnections：在任何给定的时间内，服务器将接受和处理的最大连接数。当这个数字已经达到时，服务器将接受但不处理，等待进一步连接。NIO与NIO2的默认值为10000，APR默认值为8192。
	connectionTimeout：当请求已经被接受，但未被处理，也就是等待中的超时时间。单位为毫秒，默认值为60000。通常情况下设置为30000。
	maxHttpHeaderSize：请求和响应的HTTP头的最大大小，以字节为单位指定。如果没有指定，这个属性被设置为8192（8 KB）。
	tcpNoDelay：如果为true，服务器socket会设置TCP_NO_DELAY选项，在大多数情况下可以提高性能。缺省情况下设为true
	//禁用DNS查询:
	当web应用程序向要记录客户端的信息时，它也会记录客户端的IP地址或者通过域名服务器查找机器名 转换为IP地址。
	DNS查询需要占用网络，并且包括可能从很多很远的服务器或者不起作用的服务器上去获取对应的IP的过程，这样会消耗一定的时间。
	修改server.xml文件中的Connector元素，修改属性enableLookups参数值: enableLookups="false"
	如果为true，则可以通过调用request.getRemoteHost()进行DNS查询来得到远程客户端的实际主机名，若为false则不进行DNS查询，而是返回其ip地址
	//设置解决乱码问题
	URIEncoding="UTF-8"
	
	
	这里是http connector的优化，如果使用apache和tomcat做集群的负载均衡，并且使用ajp协议做apache和tomcat的协议转发，那么还需要优化ajp connector。
	<Connector port="8009" protocol="AJP/1.3" maxThreads="600" minSpareThreads="100" maxSpareThreads="500" acceptCount="700"
	connectionTimeout="20000" redirectPort="8443" />
	
	设置session过期时间
	conf\web.xml中通过参数指定：
	<session-config>   
    	<session-timeout>180</session-timeout>     
    </session-config> 
    //单位为分钟，默认为30分钟。
	
	//2、线程池优化
	Executor代表了一个线程池，可以在Tomcat组件之间共享。使用线程池的好处在于减少了创建销毁线程的相关消耗，而且可以提高线程的使用效率。	
	 	要想使用线程池，首先需要在 Service标签中配置 Executor，如下：
		<Executor name="tomcatThreadPool"   
	         namePrefix="catalina-exec-"   
	         maxThreads="1000"   
	         minSpareThreads="100"  
	         maxIdleTime="60000"  
	         maxQueueSize="Integer.MAX_VALUE"  
	         prestartminSpareThreads="false"  
	         threadPriority="5"  
	         className="org.apache.catalina.core.StandardThreadExecutor"/> 
	其中，
	name：线程池名称，用于 Connector中指定。
	namePrefix：所创建的每个线程的名称前缀，一个单独的线程名称为 namePrefix+threadNumber。
	maxThreads：池中最大线程数。
	minSpareThreads：活跃线程数，也就是核心池线程数，这些线程不会被销毁，会一直存在。
	maxIdleTime：线程空闲时间，超过该时间后，空闲线程会被销毁，默认值为6000（1分钟），单位毫秒。
	maxQueueSize：在被执行前最大线程排队数目，默认为Int的最大值，也就是广义的无限。除非特殊情况，这个值不需要更改，否则会有请求不会被处理的情况发生。
	prestartminSpareThreads：启动线程池时是否启动 minSpareThreads部分线程。默认值为false，即不启动。
	threadPriority：线程池中线程优先级，默认值为5，值从1到10。
	className：线程池实现类，未指定情况下，默认实现类为org.apache.catalina.core.StandardThreadExecutor。如果想使用自定义线程池首先需要实现 org.apache.catalina.Executor接口。
	//配置Executor线程池，那么上面的Connector连接器就可以使用该线程池了。
	<Connector port="8080"   
    	protocol="org.apache.coyote.http11.Http11NioProtocol"
   executor="tomcatThreadPool"//使用线程池
	    maxThreads="1000"   
	    minSpareThreads="100"   
	    acceptCount="1000"  
	    maxConnections="1000"  
	    connectionTimeout="20000"   
	    maxHttpHeaderSize="8192"  
	    tcpNoDelay="true"  
	    compression="on"  
	    compressionMinSize="2048"  
	    disableUploadTimeout="true"  
	    redirectPort="8443"  
		enableLookups="false"  
	    URIEncoding="UTF-8" />  
	//可以多个connector公用1个线程池，所以ajp connector也同样可以设置使用tomcatThreadPool线程池。
	<!-- 优化配置可以多个connector公用1个线程池，所以ajp connector也同样可以设置使用tomcatThreadPool线程池。-->
	<Connector port="8009" 
		protocol="AJP/1.3" 
		executor="tomcatThreadPool"
		maxThreads="1000" 
		minSpareThreads="100" 
		maxSpareThreads="1000" 
		acceptCount="1000"
		connectionTimeout="20000" 
		redirectPort="8443" />
	
		//3、Listener
		另一个影响Tomcat 性能的因素是内存泄露。
		Server标签中可以配置多个Listener，其中 JreMemoryLeakPreventionListener是用来预防JRE内存泄漏。
		此Listener只需在Server标签中配置即可，默认情况下无需配置，已经添加在 Server中。
		
		//4、Apr插件提高Tomcat性能
		Tomcat可以使用APR来提供超强的可伸缩性和性能，更好地集成本地服务器技术.
		APR(Apache Portable Runtime)是一个高可移植库，它是Apache HTTP Server 2.x的核心。
		APR有很多用途，包括访问高级IO功能(例如sendfile,epoll和OpenSSL)，
		OS级别功能(随机数生成，系统状态等等)，本地进程管理(共享内存，NT管道和UNIX sockets)。
		这些功能可以使Tomcat作为一个通常的前台WEB服务器，能更好地和其它本地web技术集成，
		总体上让Java更有效率作为一个高性能web服务器平台而不是简单作为后台容器。
		在产品环境中，特别是直接使用Tomcat做WEB服务器的时候，应该使用Tomcat Native来提高其性能  
		要测APR给tomcat带来的好处最好的方法是在慢速网络上（模拟Internet），
		将Tomcat线程数开到300以上的水平，然后模拟一大堆并发请求。
		如果不配APR，基本上300个线程狠快就会用满，以后的请求就只好等待。
		但是配上APR之后，并发的线程数量明显下降，从原来的300可能会马上下降到只有几十，新的请求会毫无阻塞的进来。
		 在局域网环境测，就算是400个并发，也是一瞬间就处理/传输完毕，但是在真实的Internet环境下，
		 页面处理时间只占0.1%都不到，绝大部分时间都用来页面传输。
		 如果不用APR，一个线程同一时间只能处理一个用户，势必会造成阻塞。所以生产环境下用apr是非常必要的。
		//众所周知APR 能大幅提高 tomcat 的性能
		什么是APR?
		Apache可移植运行时（ Apache Portable Runtime，简称APR）是Apache HTTP服务器的支持库，
		提供了一组映射到下层操作系统的API。如果操作系统不支持某个特定的功能，APR将提供一个模拟的实现。
		这样程序员使用APR编写真正可在不同平台上移植的程序。
		什么是 tomcat-native?
		tomcat-native 库为 Tomcat 提供了本地实现。 
		tomcat-native 库依赖于三个组件：APR, OPENSSL, JDK。
		大致思路就是通过 tomcat-native 库，使tomcat运行时通过APR更多的调用本地API，
		达到提升性能的目的。
		由于依赖关系，安装时，先安装 APR, OPENSSL， 然后再安装 tomcat-native, 
		最后配置 tomcat 启动时依赖的库路径。
		tomcat7如果没配置APR，启动时会报这样的错误日志：
		信息: The APR based Apache Tomcat Native library which allows optimal performanc e in production environments was not found on the java.library.path: D:\Java\jdk 1.5.0_07\bin,提示没找到APR的配置。
		说白了作用就是如何在 Tomcat中使用JNI的方式来读取文件以及进行网络传输。这个东西可以大大提升Tomcat对静态文件的处理性能，同时如果你使用了HTTPS方式传输的话，也可以提升SSL的处理性能。可以不使用Apache也能提高对静态文件的处理能力。
		Tomcat可以使用APR来提供超强的可伸缩性和性能，更好地集成本地服务器技术.
		//Windows环境下：
		　　APR需要安装三个组件：
		　　   1、APR library
		　　   2、JNI wrappers for APR used by Tomcat (libtcnative)
		　　   3、OpenSSL libraries
		从http://tomcat.heanet.ie/native/1.1.14/binaries/win32/ ，下载tcnative-1.1.10.dll，tcnative-1.1.10.dll已经包含了上面的三个组件，所以只要把tcnative-1.1.10.dll拷贝到tomcat的bin下就行了。
		(1)目前的tomcat7.053的bin下有tcnative-1.dll，该dll库包含了上面的三个组件。
		然后启动tomcat，启动后的信息为：信息: Loaded APR based Apache Tomcat Native library 1.1.14. 2009-12-24 14:17:59 org.apache.catalina.core.AprLifecycleListener init 信息: APR capabilities: IPv6 [false], sendfile [true], accept filters [false], r andom [true]. 2009-12-24 14:18:00 org.apache.coyote.http11.Http11AprProtocol init 这说明APR配置成功，已经启用。
		(2)设置 Tomcat 整合 APR
		修改 tomcat 的启动 shell （ catalina.sh ），进入该文件搜索CATALINA_OPTS会找到相关说明：
		在该文件中加入启动参数： CATALINA_OPTS="-Djava.library.path=/usr/local/apr/lib"
		(3)配置Connector的协议protocol="org.apache.coyote.http11.Http11AprProtocol"
		<Connector port="8080"   
    	protocol="org.apache.coyote.http11.Http11NioProtocol"
    	executor="tomcatThreadPool"
	    maxThreads="1000"   
	    minSpareThreads="100"   
	    acceptCount="1000"  
	    maxConnections="1000"  
	    connectionTimeout="20000"   
	    maxHttpHeaderSize="8192"  
	    tcpNoDelay="true"  
	    compression="on"  
	    compressionMinSize="2048"  
	    disableUploadTimeout="true"  
	    redirectPort="8443"  
		enableLookups="false"  
	    URIEncoding="UTF-8" />  
		
		判断成功:
			org.apache.catalina.core.AprLifecycleListener init
			OpenSSL successfully initialized (OpenSSL 1.0.1e 11 Feb 2013)
			Initializing ProtocolHandler ["http-apr-8080"]
	}
	
	
	public void 如何配置图形界面查看tomcat内存使用情况(){
		在TOMCAT主目录中，进入conf文件夹，找到tomcat-users.xml文件，并打开：
		<!-- 如下配置默认为注释状态，如果要查看Tomcat的内存使用情况可以打开这里
		  <role rolename="tomcat"/>
		  <role rolename="role1"/>
		  <user username="tomcat" password="tomcat" roles="tomcat"/>
		  <user username="both" password="tomcat" roles="tomcat,role1"/>
		  <user username="role1" password="tomcat" roles="role1"/>
		  -->
		  
		  <!-- 修改成如下：-->
		  <role rolename="manager-gui"/>
		  <role rolename="role1"/>
		  <user username="admin" password="jobs1226" roles="manager-gui"/>
		  <user username="both" password="tomcat" roles="tomcat,role1"/>
		  <user username="role1" password="tomcat" roles="role1"/>
		  
		  通过访问：localhost:端口/manager/status，比如：http://localhost:8080/manager/status
		 输入用户名：admin,密码：jobs1226登陆。
	}
	
	public void tomcat假死排查方案(){
		参考：https://www.cnblogs.com/banning/p/6346938.html
		1、首先确认页面端正常时请求没有问题
	　　	2、对于使用Nginx作为前端负载均衡Tomcat集群，通过Nginx的访问日志(access.log)确认页面到Nginx没有问题
	　　	3、查看Tomcat的访问日志(localhost_access_log.txt)查看前端请求是否访问到Tomcat
	　　	4、查看Tomcat的状态控制台，查看Tomcat的请求和内存占用情况：通过访问：localhost:端口/manager/status
	　　	5、查看数据库进程查看当前数据库实例是否出现死锁，在数据库中新建查询：执行：SHOW FULL PROCESSLIST;
	  	6、如果以上都没有问题，排查代码中的定时任务
	　　　　查看定时任务事务是否存在问题(长事务、频繁事务)
	　　	7、检查是否是频繁的写日志造成Tomcat阻塞
	　　　　对于访问量比较大的系统，如果项目采用Info或者Debug日志级别的话会造成Tomcat频繁的读写几百兆甚至上G的日志文件，造成Tomcat阻塞
	          
	          曾经遇到的一次Tomcat出现CLOSE_WAIT时，
	          通过排查发现是一位同事在通过定时任务做其他系统的数据同步时时出现的问题造成的，问题总结如下：
	  　　1、同步其他系统的数据是耗时较长，其采用Spring的切面事务，造成同步时事务时间长达几分钟时间，存在死锁风险
	  　　2、同步数据完需要处理数据，此时的处理数据逻辑会存在多达几万次的数据库变更，当前操作没有采用切面事务，而是采用框架的AutoCommit自动提交事务，这样就会造成处理数据时出现几万次的创建事务，提交事务，关闭事务，此时造成事务阻塞
	    解决方案：
	  　　1、处理时间较长的操作，如果当前操作中间出现问题对业务没有影响下次操作时会修正当前业务，
	    	这样的话可以不适用切面事务而是使用框架的AutoCommit提交事务，
	    	如果当前操作确实需要保证原子性时，请手动回复数据装填。
	  　　2、不要频繁的开启、提交事务，采用批量的方式提交事务。

	   
	}
	/**
	 * tomcat bio nio apr 模式性能测试
	 * 参考：https://www.cnblogs.com/drizzlewithwind/p/6072029.html
	 */
	public void 模式性能测试(){
		11.11活动当天，服务器负载过大，导致部分页面出现了不可访问的状态、那后来主管就要求调优了，下面是tomcat bio、nio、apr模式以及后来自己测试的一些性能结果。
		原理方面的资料都是从网上找的，并且把多个地方的整理到了一起，觉得很有意义。
		（后面对tomcat默认页面测试的数据是自己测出来的），tomcat 的三种模式如果用对了场合，
		性能绝对有大幅度的提升。当然调优也并不只在这一个方面，
		还有内存（堆内存、非堆内存、新生代内存）以及线程（最大线程、请求队列、备用线程、压缩、以及禁用dns轮询）等方面。
		那在做tomcat bio nio apr 模式之前，先来了解下 Java 的一些特性吧。
		
		同步 ： 自己亲自出马持银行卡到银行取钱（使用同步IO时，Java自己处理IO读写）。
		异步 ： 委托一小弟拿银行卡到银行取钱，然后给你（使用异步IO时，Java将IO读写委托给OS处理，需要将数据缓冲区地址和大小传给OS(银行卡和密码)，OS需要支持异步IO操作API）。
		阻塞 ： ATM排队取款，你只能等待（使用阻塞IO时，Java调用会一直阻塞到读写完成才返回）。
		非阻塞 ： 柜台取款，取个号，然后坐在椅子上做其它事，等号广播会通知你办理，没到号你就不能去，你可以不断问大堂经理排到了没有，大堂经理如果说还没到你就不能去（使用非阻塞IO时，如果不能读写Java调用会马上返回，当IO事件分发器会通知可读写时再继续进行读写，不断循环直到读写完成）。
		
		Java对BIO、NIO、AIO的支持：
		Java BIO ： 同步并阻塞，服务器实现模式为一个连接一个线程，即客户端有连接请求时服务器端就需要启动一个线程进行处理，如果这个连接不做任何事情会造成不必要的线程开销，当然可以通过线程池机制改善。
		Java NIO ： 同步非阻塞，服务器实现模式为一个请求一个线程，即客户端发送的连接请求都会注册到多路复用器上，多路复用器轮询到连接有I/O请求时才启动一个线程进行处理。
		Java AIO(NIO.2) ： 异步非阻塞，服务器实现模式为一个有效请求一个线程，客户端的I/O请求都是由OS先完成了再通知服务器应用去启动线程进行处理

		BIO、NIO、AIO适用场景分析:
		BIO方式适用于连接数目比较小且固定的架构，这种方式对服务器资源要求比较高，并发局限于应用中，JDK1.4以前的唯一选择，但程序直观简单易理解。
		NIO方式适用于连接数目多且连接比较短（轻操作）的架构，比如聊天服务器，并发局限于应用中，编程比较复杂，JDK1.4开始支持。
		AIO方式使用于连接数目多且连接比较长（重操作）的架构，比如相册服务器，充分调用OS参与并发操作，编程比较复杂，JDK7开始支持。
		
		下面来看看tomcat 的 bio、nio、apr 模式
		bio 
		bio(blocking I/O)，顾名思义，即阻塞式I/O操作，表示Tomcat使用的是传统的Java I/O操作(即java.io包及其子包)。Tomcat在默认情况下，就是以bio模式运行的。遗憾的是，就一般而言，bio模式是三种运行模式中性能最低的一种。我们可以通过Tomcat Manager来查看服务器的当前状态。

		nio 
		是Java SE 1.4及后续版本提供的一种新的I/O操作方式(即java.nio包及其子包)。Java nio是一个基于缓冲区、并能提供非阻塞I/O操作的Java API，因此nio也被看成是non-blocking I/O的缩写。它拥有比传统I/O操作(bio)更好的并发运行性能。

		apr 
		(Apache Portable Runtime/Apache可移植运行库)，是Apache HTTP服务器的支持库。你可以简单地理解为，Tomcat将以JNI的形式调用Apache HTTP服务器的核心动态链接库来处理文件读取或网络传输操作，从而大大地提高Tomcat对静态文件的处理性能。 Tomcat apr也是在Tomcat上运行高并发应用的首选模式。
		
		通过配置完重启，通过ip:port/manager/status 就可以看tomcat状态了，里面有服务器的信息及tomcat信息。
		//bio server.xml 配置 （重启生效）
		<Connector port="8080" protocol="HTTP/1.1"
        connectionTimeout="20000"
        redirectPort="8443" />
		//nio server.xml 配置 （重启生效）
		<Connector port="8080" protocol="org.apache.coyote.http11.Http11NioProtocol"
        connectionTimeout="20000"
        redirectPort="8443" />
		//apr server.xml 配置 （重启生效）
		<Connector port="8080" protocol="org.apache.coyote.http11.Http11AprProtocol"
        connectionTimeout="20000"
        redirectPort="8443" />
		
		到这里大致的配置就结束了，建议在做实验之前，先了解清楚java的 bio 、nio 、aio特性，
		在web服务器上阻塞IO(BIO)与NIO一个比较重要的不同是，
		客户系统使用BIO的时候往往会为每一个web请求引入多线程，每个web请求一个单独的线程，
		所以并发量一旦上去了，线程数就上去了，CPU就忙着线程切换，
		所以BIO不合适高吞吐量、高可伸缩的web服务器；
		而NIO则是使用单线程(单个CPU)或者只使用少量的多线程(多CPU)来接受Socket，
		而由线程池来处理堵塞在pipe或者队列里的请求.这样的话，只要OS可以接受TCP的连接，
		web服务器就可以处理该请求。大大提高了web服务器的可伸缩性。
		可以看到，随着线程的不断增多，bio 模式性能越来越差，就算是在本地，
		错误率和响应时间都在明显的增加、而吞吐量、样本数和每秒传输速率都在下降（当然，如果是生产环境，
		我们肯定通过nginx web 软件进行反向代理，提供多个tomcat 节点来提供更稳定的服务。）
		而 nio 和 apr模式基本上没有变化太多，都保持在一个稳定的状态。
		而后来当我进行一些 service 后端程序的测试时，
		发现 tomcat 性能并没有大幅度的提升，甚至会有下降的趋势。
		（该接口不是在同网段测试，而是跨越路由器，在网络传输中会有损耗方面，性能跟本地测试也会有所差异）
		个人觉得在 tomcat bio、nio、apr 模式中，每种都会有各自适用的场合，也不能说哪个好那个不好，就像 tomcat 内存方面的配置，如果内存设置的过大，gc 垃圾回收机制就会变慢；如果内存设置的过小，tomcat又会出现内存溢出的情况，所以设置在一个合适的范围很重要，不仅不会出错，并且gc回收频繁使性能达到一个最优的结果。当然，这也需要根据不同的场合进行不同的测试才能产生最优的结果！
	}
	
	public void 一台电脑同时配置N个tomcat(){
		//一台电脑同时配置N个tomcat 
	    1.使用压缩版的tomcat不能使用安装版的。 
	    2.第一个tomcat的配置不变。 也可以修改catalina.bat，增加下面的代码，使其不要参考环境变量。各自自己设定相关路径。
	    3.增加环境变量CATALINA_HOME2，值为新的tomcat的地址；增加环境变量CATALINA_BASE2，值为新的tomcat的地址。
	    4.修改新的tomcat中的startup.bat，把其中的CATALINA_HOME全部修改为CATALINA_HOME2。 
	    5.修改新的tomcat中的catalina.bat，把其中的CATALINA_HOME改为CATALINA_HOME2，CATALINA_BASE改为CATALINA_BASE2。 
		5.2修改新的tomcat中的catalina.bat,在文件开头加入如下代码：
			设置每个tomcat不要参考环境变量的JAVA_HOME,JRE_HOME,TOMCAT_HOME并自己设置路径和内存情况。
			SET JAVA_HOME=D:\SnowServer\jdk1.8
			SET JRE_HOME=D:\SnowServer\jdk1.8\jre
			SET TOMCAT_HOME=E:\apache-tomcat-9-pillbox
			set JAVA_OPTS=-server -Xmx2048m -XX:MetaspaceSize=512m -XX:MaxMetaspaceSize=768m -Xss128m
	    6.修改conf/server.xml文件： 设置每个tomcat各自不同的启动和关闭的端口，防止端口冲突。比如如下设置：
	    6.1 <Server port="8005" shutdown="SHUTDOWN"> 此端口改为8006
	    6.2 <Connector port="8080" maxHttpHeaderSize="8192"  
	          maxThreads="150" minSpareThreads="25" maxSpareThreads="75"  
	          enableLookups="false" redirectPort="8443" acceptCount="100"  
	          connectionTimeout="20000" disableUploadTimeout="true" /> 把8080端口改为没有是使用的端口，如8888。
	    6.3<Connector port="8009"  
	          enableLookups="false" redirectPort="8443" protocol="AJP/1.3" />此端口改为8010
	    7  成功！
		可以通过debug的方式看看tomcat的启动是否正常：catalina jpda start
		
		注意：2个tomcat或多个tomcat，如果要做成服务，注意每次都要修改CATALINA_HOME、CATALINA_BASE环境变量，保证每个
		服务对应的CATALINA_HOME、CATALINA_BASE不一样，对应自己的环境，以免冲突。
	}
	
	public void tomcat做成系统服务(){
		1. 在DOS命令行模式下，cd到tomcat的bin目录下。或者直接去到bin目录下，按住shift+鼠标右键选择“在此处打开命令行窗口”
		2.在tomcat的bin目录下可以看到有一个service.bat的批处理文件，这个文件就是为我们将tomcat注册成系统服务所用。有兴趣的可以打开看看批处理文件里面的内容。
		3.执行命令service.bat  install  服务名   后面的服务名可以随便取，别跟系统已有的服务名冲突就行。
		如果成功可以看到以下提示信息：
		The Service '服务名'  has been installed
		
		如果不需要这个服务了，也可以用简单的命令将其去除就OK 了
		前面步骤如上，执行命令：service.bat uninstall 服务名
		移除成功的提示信息：
		The Service ‘服务名' has been removed
	}
	
	
	public void tomcat编码设置(){
		今天开发跟我说tomcat日志中的中文不能正常显示，根据以往的经验，我觉得可能跟服务器的编码有关。
		修改tomcat/bin/catalina.sh文件，在JAVA_OPTS变量后新增以下参数：
		-Dfile.encoding=UTF8 -Dsun.jnu.encoding=UTF8
		然后重启tomcat即可。
		如果在eclipse里，则删掉tomcat重新添加即可。
	}
}