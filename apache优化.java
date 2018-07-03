/**
 * 1、什么产品? apache
 * 
 * 2、为什么用? apache服务器能解析静态文件（php/html/图片等）性能好，还可以集成Tomcat、jboss等web服务器，
 * 进行集群环境的搭建，提供很好的服务器支持。
 * 
 * 3、怎么使用? 下载并安装，并优化apache服务器，主要配置conf/httpd.conf。
 * 
 * 4、有什么好处和心得? PHPStudy产品集成了apache，iis等环境，实际使用时可以使用PHPStudy来部署服务器环境。
 * 做apache的优化、做tomcat的优化、定时关闭并重启数据库，定时关闭apache服务器并重启。
 * 
 * @author jobs1127
 *
 */
public class apache{
	/**
	 * 两种方法轮着试试，服务器不同可能支持不同。
	 * 参考：http://blog.51cto.com/zhangqi/427196
	 */
	public void windows下定时重启apache和MySQL(){
		//采用at命令是老的设置方法，适合老的机器，如果该方法不行，则使用下面的采用schtasks命令设置的方法
		采用at命令添加计划任务
		1、在c:盘根目录新建一个apachemysqlautostart.bat文件，然后右键编辑，将文件内容设为如下后保存：
		//apacheautostart.bat文件内容：
		@ECHO OFF
		net stop apache2
		net start apache2
		net stop mysql
		net start mysql
		
		//上面的apache2与mysql为服务名称，服务器名称后面最好不要有空格。
		
		开始”－》以管理员的身份运行“cmd”－》执行命令“at 04:00 c:\apachemysqlautostart.bat”
		这样每天早晨4点系统就会自动重启apache与mysql。
		（附加说明：04:00为重启时间，c:\autostartserver.bat为第一步的bat文件地址）
		mysql的重新启动，也通过上面at命令方法重新启动了.
		
		//方法2 windows server 2012R2等服务器 
		1、在c:盘根目录新建一个apachemysqlautostart.bat文件，然后右键编辑，将文件内容设为如下后保存：
		//apacheautostart.bat文件内容：
		@ECHO OFF  
		net stop apache2
		net start apache2
		net stop mysql
		net start mysql   
		
		开始”－》以管理员的身份运行“cmd”－》执行命令：
		schtasks /create /tn "apachemysqlautostart" /tr c:\apachemysqlautostart.bat /sc daily /st 04:17  
		
		
		//有时候中间需要一定的间隔时间，可以通过下面的代码实现。
		net stop sqlserveragent 
		net stop MSSQLSERVER 
		echo.wscript.sleep(50000)>s.vbs 
		cscript //nologo s.vbs 
		del s.vbs 
		net start mssqlserver 
		net start sqlserveragent
	}
	public void windows下apache优化(){
		通过cmd进入apache的安装目录bin目录下执行命令。
		//查看当前安装模块mpm(多路处理器)：httpd -l  
		Compiled in modules:
			  core.c
			  mod_win32.c
			  mpm_winnt.c
			  http_core.c
			  mod_so.c
		/***
		 * 在复杂的网络环境中，浏览器是百花齐放，各式各样。
		 * 目前还有微信和QQ等内嵌浏览器，使用中我们的Apache会遇到不同的问题，
		 * 今天就遇到了一个微信浏览器访问站点导致网站变慢以至apache挂起的情况，
		 * 试验中我们也发现IE10浏览器也经常会导致同样的问题。	
		 */
		//1、打开Apache的\conf\httpd.conf 查找
		#AcceptFilter http none
		#AcceptFilter https none
		修改为
		AcceptFilter http none
		AcceptFilter https none
		默认添加了这个配置只是注释掉了，我们去掉#号，开启配置。
		此处可以解决某些浏览器导致apache慢或者假死不响应的情况，提高兼容性。
		
		//2、Apache2.4下\conf\original\extra 找到httpd-mpm.conf 查找
		修改为：
		<IfModule mpm_winnt_module>
		    ThreadsPerChild      500
		    MaxRequestsPerChild  10000
		    Win32DisableAcceptEx
		</IfModule>
		进程自apache启动，可以同时起多少线程(ThreadsPerChild)。
		MaxRequestsPerChild的含义是单个子进程累计最多处理到少个请求，默认0，不限制的意思，可能会导致内存泄露，
		超过该值则退出重启apache。
		ThreadsPerChild 数目一般100-500
		acceptEx()是一个微软的WinSock2API， 通过使用accept() API提供了性能改善。
		一些防病毒软件或虚拟专用网络软件会干扰AcceptEX()的正确操作。
		可以关闭AcceptEx()：Win32DisableAcceptEx。phpstudy下的apache不能配置该项。
		1.Apache在编译时内部有一个硬性的限制"ThreadLimit 20000"(对于mpm_winnt是"ThreadLimit 15000")，你不能超越这个限制。
		2.将MaxRequestsPerChild设置成非零值有两个好处：
		a)可以防止(偶然的)内存泄漏无限进行，从而耗尽内存。
		b)给进程一个有限寿命，从而有助于当服务器负载减轻的时候减少活动进程的数量。
		
		//二、在httpd.conf中，去掉注释 Include conf/extra/httpd-default.conf

		编辑 httpd-default.conf
		Timeout 20                  #默认是300，缩小该参数就会减少同时连接数
		KeepAlive On                #默认是On，该参数为是否保持活连接，目前网站中一个页面一般会包含多个文件，所以相应用户访问时会有多个请求，因此开启可以提高服务器性能。
		MaxKeepAliveRequests 100    #默认是100，最大的活连接请求数，可以根据网页实际包含的文件数目自行调节。
		KeepAliveTimeout 5          #默认是5，活动连接的超时时间，一般只要设置成小于Timeout即可。
		
		//3、在httpd.conf 查找
		备注：如果加了AcceptFilter https none
		在不能使用ssl的情况下，应该注释为#AcceptFilter https none
		
	}
	
	public void 从命令行cmd启动apache能知道错误在哪一行(){
		apache的安装路径：D:\Apache24\
		cmd进去到黑窗口，apache_http_server这个是你的apache的名称
		D:\Apache24\bin>httpd.exe -w -n "apache_http_server" -k start
	}
	public void 监测apache运行状态(){
		//开启Apache的server status监测
		从httpd.conf 打开 status_module
		#LoadModule status_module modules/mod_status.so
		修改成
		LoadModule status_module modules/mod_status.so
		
		而apache2.2以上版本中的Order、Allow等命令在新版本中也可以得到兼容，
		实现这个兼容功能的模块就是 mod_access_compat。
		所以Load这个模块后，apache2.4就能识别这些语句了。
		从httpd.conf 打开mod_access_compat
		LoadModule access_compat_module modules/mod_access_compat.so
		默认是注释掉的，修改成打开状态。
		
		在httpd.conf文件里添加如下代码：
		ExtendedStatus On      ##获得一个完整的报告与当前状态信息
		<IfModule status_module> 
        	<Location /server-status> ##访问地址
                SetHandler server-status
                Order Deny,Allow
                Deny from all
                Allow from all ##允许访问apache运行状态的IP, all代表不限制ip
            </Location>
        </IfModule>
        
                       如果看不了尝试修改Directory：修改如下：
	     <Directory >
	    	 Options FollowSymLinks
	    	 AllowOverride None
	    	 Order deny,allow
	         allow from all
	         Satisfy all
	     </Directory>
        
		使用http://127.0.0.1/server-status来访问，如果需要自动更新，可以用http://127.0.0.1/server-status?refresh=N，N是更新时间，默认是秒
		http://127.0.0.1/server-status?refresh=5  ##server-status可以自定义地址，5秒自动刷新
		成功案例：http://127.0.0.1/server-status
			
		server-status 的输出中每个字段所代表的意义如下：
		字段                       说明
		Server Version       Apache 服务器的版本。
		Server Built            Apache 服务器编译安装的时间。
		Current Time          目前的系统时间。
		Restart Time           Apache 重新启动的时间。
		Parent Server Generation       Apache 父程序 (parent process) 的世代编号，就是 httpd 接收到 SIGHUP 而重新启动的次数。
		Server uptime         Apache 启动后到现在经过的时间。
		Total accesses        到目前为此 Apache 接收的联机数量及传输的数据量。
		CPU Usage            目前 CPU 的使用情形。
		_SWSS....       所有 Apache process 目前的状态。每一个字符表示一个程序，最多可以显示 256 个程序的状态。
		Scoreboard Key       上述状态的说明。以下为每一个字符符号所表示的意义：
		 
		* _：等待连结中。
		* S：启动中。
		* R： 正在读取要求。
		* W：正在送出回应。
		* K：处于保持联机的状态。
		* D：正在查找 DNS。
		* C：正在关闭连结。
		* L：正在写入记录文件。
		* G：进入正常结束程序中。
		* I：处理闲置。
		* .：尚无此程序。
		 
		Srv       本程序与其父程序的世代编号。
		PID       本程序的 process id。
		Acc       分别表示本次联机、本程序所处理的存取次数。
		M       该程序目前的状态。
		CPU       该程序所耗用的 CPU 资源。
		SS       距离上次处理要求的时间。
		Req       最后一次处理要求所耗费的时间，以千分之一秒为单位。
		Conn       本次联机所传送的数据量。
		Child       由该子程序所传送的数据量。
		Slot       由该 Slot 所传送的数据量。
		Client       客户端的地址。
		VHost       属于哪一个虚拟主机或本主机的 IP。
		Request       联机所提出的要求信息。
		 
		查看Apache的请求数和开启Apache Server Status 
		在Linux下查看Apache的负载情况，最简单有有效的方式就是查看Apache Server Status，在没有开启Apache Server Status的情况下，或安装的是其他的Web Server，比如Nginx的时候，下面的命令就体现出作用了。
		ps -ef|grep httpd|wc -l命令
		#ps -ef|grep httpd|wc -l
		1388
		统计httpd进程数，连个请求会启动一个进程，使用于Apache服务器。
		表示Apache能够处理1388个并发请求，这个值Apache可根据负载情况自动调整，我这组服务器中每台的峰值曾达到过2002。
		 
		netstat -nat|grep -i “80″|wc -l命令
		#netstat -nat|grep -i “80″|wc -l
		4341
		netstat -an会打印系统当前网络链接状态，而grep -i “80″是用来提取与80端口有关的连接的, wc -l进行连接数统计。
		最终返回的数字就是当前所有80端口的请求总数。
		 
		netstat -na|grep ESTABLISHED|wc -l命令
		#netstat -na|grep ESTABLISHED|wc -l         ---------个人测试此命令比较准确
		376
		netstat -an会打印系统当前网络链接状态，而grep ESTABLISHED 提取出已建立连接的信息。 然后wc -l统计。
		最终返回的数字就是当前所有80端口的已建立连接的总数。
		 
		netstat -nat||grep ESTABLISHED|wc -   可查看所有建立连接的详细记录
		 
		查看Apache的并发请求数及其TCP连接状态：
		Linux命令：
		netstat -n | awk '/^tcp/ {++S[$NF]} END {for(a in S) print a, S[a]}'
		（这条语句非常不错）
		返回结果示例：
		LAST_ACK 5
		SYN_RECV 30
		ESTABLISHED 1597
		FIN_WAIT1 51
		FIN_WAIT2 504
		TIME_WAIT 1057
		其中的SYN_RECV表示正在等待处理的请求数；ESTABLISHED表示正常数据传输状态；TIME_WAIT表示处理
		完毕，等待超时结束的请求数。
	}
	
	/**
	 * 搭建apache2.4+php5.6环境，如果使用PHPStudy，则无需自己集成PHP环境。PHPStudy已经集成好了。
	 */
	public void 搭建apache集成php环境(){
		工具下载地址
		apache2.4 下载地址：http://httpd.apache.org/
		php5.6 下载地址：http://windows.php.net/download#php-5.6
		一、安装apache2.4
		第一步 解压apache2.4
		第二步 安装apache服务
		这里默认安装在D盘Apache2.4目录下，命令如下://通过cmd窗口，进入apache的安装目录
			cmd黑窗口 ：
			C:\Windows\system32>d:
			D:\>cd apache2.4
			D:\Apache2.4>cd bin
			D:\Apache2.4\bin>httpd.exe -k install -n "apache24"
			安装的Apache名字叫做 apache24，安装成功后，在我的电脑，管理/服务/就能找到名为Apache24的Apache服务器了，在这里可以启动关闭等操作。
			参考:http://blog.csdn.net/py_xin/article/details/50564339
		注意：如果apache目录不在盘符根目录下，需要修改conf下的httpd.conf，修改Define SRVROOT 指定安装目录
		Define SRVROOT "D:/Apache2.4"
		ServerRoot "${SRVROOT}"
		第三步 测试Apache容器是否安装成功
		win+r 输入service.msc 查看服务是否安装成功
		
		二 、安装PHP
		php的解压目录：D:\web_project\PHP5.6
		这里以PHP作为apache模块进行安装
		第一步 解压php
		第二步 简单配置PHP,php的解压目录，找到php.ini文件，
		搜索extension_dir 修改为
		extension_dir = "D:\web_project\PHP5.6\ext"
		修改php时区搜索date.timezone 修改为
		date.timezone =PRC
		
		三、在Apache中引入PHP模块
		第一步 修改Apache conf目录下的httpd.conf 添加如下代码
		LoadModule php5_module "D:/web_project/PHP5.6/php5apache2_4.dll"
		PHPIniDir "D:/web_project/PHP5.6"
		AddType application/x-httpd-php .php .html .htm
		
		第二步 修改索引页
		搜索DirectoryIndex找到以下代码
		<IfModule dir_module>
		    DirectoryIndex index.html
		</IfModule>
		修改为
		<IfModule dir_module>
		    DirectoryIndex index.php index.html
		</IfModule>
		   
		四、测试PHP模块加载是否成功
		    在Apache安装目录下的htdocs文件夹下新建index.php文件，编辑文件添加如下代码
		    <?php
		    phpinfo();
		    ?>
		重启Apache服务器，在地址栏输入 localhost 看到PHP相关配置，说明配置成功。
	}
	public void Apache和Tomcat整合() {
		//学习参考：http://blog.csdn.net/stefyue/article/details/6918542
		为什么要做这个整合呢？当然，首先想到是就是Apache和Tomcat的区别。
		正因为有区别，有各自的优缺点才需要整合，取二者所长，弃二者所短。
		Apache擅长处理静态文件，对动态网页显得无能无力，
		Tomcat也可以作为独立的web服务器来运行。但Tomcat也是应用（java）服务器，它只是一个Servlet容器。
		由于Apache解释静态页面要比tomcat快速而且稳定， 基于以上原因，一个现实的网站使用一个Apache作为Web服务器，为网站的静态页面请求提供服务；
		并使用Tomcat服务器作为一个Servlet/JSP插件，显示网站的动态页面。
		
		Apache+Tomcat整合的好处:
			1.Apache主要用来解析静态文本,如html，tomcat虽然也有此功能，
				但apache能大大提高效率，对于并发数较大的企业级应用，能更好的显示apache的高效率；
			2.Tomcat用来解析jsp,servlet等,所有的客户请求首先会发送到Apache，
				如果请求是静态文本则由apache解析，并把结果返回给客户端，
				如果是动态的请求，如jsp，apache会把解析工作交给tomcat，
				由tomcat进行解析（这首先要两者现实整合），tomcat解析完成后，
				结果仍是通过apache返回给客户端，这样就可以达到分工合作,
				实现负载均衡，提高系统的性能！而且因为JSP是服务器端解释代码的，
				这样整合可以减少Tomcat的服务开销。
		
		Apache+Tomcat整合的原理
		作为Apache下面的子项目，Tomcat与 Apache之间有着天然的联系。
		在实际操作中，主要是Apache作为主服务器运行，当监听到有jsp或者servlet的请求时，
		将请求转发给tomcat服务器，由tomcat服务器进行解析后，发回apache，再由apache发回用户。
		在tomcat中有两个监听的端口，一个是8080用于提供web服务,
		一个是8009用于监听来自于apache的请求。
		当apache收到jsp或者servlet请求时，就向tomcat的8009端口发送请求，交由tomcat处理后，
		再返回给apache，由apache返回给客户。
		
		Jk(Tomcat Connector)是apache和tomcat的连接器，也可以做负载均衡器，
		主要是apache通过jk找到tomcat。
		
		如何安装Apache：
		下载Apache，解压，如D:/Apache2.4
		cmd.exe用管理员的身份运行打开cmd，然后进入到解压开的Apache2.4目录，进入bin目录：
		cmd黑窗口 ：
			C:\Windows\system32>d:
			D:\>cd apache2.4
			D:\Apache2.4>cd bin
			D:\Apache2.4\bin>httpd.exe -k install -n "apache24"
		安装的Apache名字叫做 apache24，安装成功后，在我的电脑，管理/服务/就能找到名为Apache24的Apache服务器了，在这里可以启动关闭等操作。
		参考:http://blog.csdn.net/py_xin/article/details/50564339
		
		项目案例：参考Digest_wx_apache_integrate_tomcat项目。
	}
	
	
	
	
	
}