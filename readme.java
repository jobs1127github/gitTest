import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sf.json.JSONObject;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.tentinet.app.bean.dto.InformationDto;
import com.tentinet.app.excel.model.Person;
import com.tentinet.app.util.ConfigUtil;
import com.tentinet.app.util.Page;
import com.tentinet.weixin.entity.WXUrl;

public class readme { 
	public void A开发过程() {
		1、确定需求，尽量详细的需求分析，根据需求确定编程语言，选好框架。
		2、新建工程，并设置工作空间的content-types编码为utf-8，工程的属性resource的编码为utf-8。统一编码。
		3、定好约定（各种命名遵守一定的规范）。
		4、建立界面原型（抽象相关的类，把建立相关的联系，在封装类中的属性和方法）
		5、建立数据库、实体类
		6、建立service层DAO层，可能还有DTO层、然后根据前台框架，建立控制层。
	}
	
	public void A请求过程() {
		url请求->中间件（tomcat、jboss、Apache）服务器
		->
		分发到指定的Controller控制器（前台框架，比如：Struts2、SpringMVC、JSF2.0等），
		控制器用@Controller标识控制层，通过Spring容器管理。
		->
		业务接口（通过@Service标识service层）->
		DAO（service接口会调用DAO去具体处理，通过@Repository标识DAO层，或叫持久层），DAO处理具体业务时，
		可以通过Hibernate来完成，也可以通过iBaties、jdo等ORM（对象、关系映射）框架处理。
		->
		DB（数据库、关系）->获得数据->再有控制层对结果进行处理，根据情况交由具体的页面进行展示到浏览器。
	}
	
	public void A和前台页面建立关系() {
		1、JSF2.0集成了ajax，不用自己写ajax代码 控制层@ManagedBean(name = "nssCommercials")，
		可以把整个控制层类放在某个范围里，如：@SessionScoped，
		使得这类里的成员变量都被放置在了session里不用自己去关心session的存储值。
		<h:commandButton value="经理上传Excel导入盘库库存↑" action="#{nssCommercials.fileUploadPrepreToMarketPlanning}" styleClass="button white medium" rendered="false">
		<f:ajax render="batchTopankukucun:upload_filepankukucn msg_all" onevent="showFileUploads_pankukun"/>
		</h:commandButton>。
		
		2、Struts2控制层是action，一般情况extends ActionSupport，
		通过Struts.xml配置文件规划好请求，请求中指定了调用的action的方法，默认调用execute()方法。
		
		3、SpringMVC在控制层里通过@RequestMapping来和前台页面互通，
		在RequestMapping里指定了要调用的方法名，这样在URL请求时就会调用该RequestMapping对应的方法。
	}
	
	public void A本项目的表之间的关系() {
		后台表：
		用户表（t_user）:user_id、user_role
		角色表（t_role）:role_id
		角色菜单表（t_role_menu）:中间表，role_id、menu_id
		菜单表（t_menu）:menu_id、parent_menu_id
		
		前台表：
		微信资讯文章（wx_information）：information_id、open_id（文章是哪个用户写的）
		information_type（文章的类型）
		微信openID用户（wx_openid_wxno）：open_id、group_id
		微信openID用户组（wx_user_group）：group_id
		数据字典表（t_data_dictionary）：t_data_type（数据的类型）、t_data_item（数据类别，可以对应文章的类型）
		文章的奖励信息（wx_information_award_info）：information_id（该奖励是哪个文章的）
		文章的浏览信息（wx_information_browse_count）：information_id（该浏览是对哪个文章的浏览）
		文章的分享信息（wx_information_share_count）：information_id（该分享是对哪个文章的分享）
	}
	
	public void A软件部署() {
		前台web服务器（Tomcat）前台转发---业务应用服务器（jboss）业务逻辑处理---数据库服务器（数据持久化）
		如果同时在线2~3万人访问系统，可以在前台web服务器进行集群，可以集群3~4台web服务器。
		具体的请求交由哪台服务器去处理，可以通过负载均衡（可以通过软件解决，也可以通过硬件解决）来调配。
		还可以在业务应用服务器进行集群。
		集群引发的问题：
		1、用户上传文件，如何保持文件在各个服务器之间的共享?
			a、通过软件解决，每台服务器通过socket编程把用户上传的文件进行同步。性能较差。
			b、通过硬件解决，可以引入磁盘阵列，该磁盘阵列相当于该集群环境中的硬盘。文件都存放到该硬盘上。
		2、session问题，session如何同步?
			a、通过软件解决，通过Tomcat+session同步，各种配置，可以实现各个服务器之间的session同步。性能较差。
			b、通过硬件解决，引入session服务器，充当集群环境中的内存。这样每台服务器都共享同一份内存。
			
		3、如何不使用session，解决像登陆、购物车等问题?
			使用cookie+DB的设计方法。
			userid     user          aceessTime
			uuid	   userObject       时间戳
			
			userid     Cart          aceessTime
			uuid	   cartObject       时间戳
		
			aceessTime可以通过写一个线程定时器，每分钟都去扫描目标表，发现超过30分中的可以从数据库中删除，模拟session过期。
			用户在访问网站时，可以通过Filter拦截过滤，给该用户分配UUID，并保存cookie。
			
		1个网站弄了2个域名，每个域名对应不同的站点，对应不同的服务器处理。
		目的：减轻Tomcat的压力，提高网站的响应速度
		
		域名1：image/js/css 这些静态文件，通过Apache http服务器解析静态文件
		域名2：jsp/servlet 通过Tomcat等web容器来解析
		
		Apache+Tomcat 整合。
		
		使用到的相关技术：
		table是先要加载完元素，才能显示里面的内容，会出现先加载一些内容，其他内容突然加载出来，如果内容比较多时，还可能造成迟迟不出来内容的情况。
		页面使用的技术：div+css 起到网页减肥的作用，布局方便，提高用户体验，方便浏览器搜索引擎的收录。
		优化性能的技术：
		1、OSCache缓存技术；产品列表可以考虑使用缓存技术，而不是页面静态化，因为查询产品列表的方式比较多，比如排序查询等，采用页面静态化的话，要做很多静态页面。
		2、Velocity模板技术，实现页面静态化（没有从数据库去加载）；产品的详情页面可以考虑使用页面静态化页面。
			模板技术：
			1、Velocity 
			2、Freemarket 比Velocity更厉害。struts2就是使用了Freemarket。
		Velocity的使用步骤：
			velocity-1.6-dep.jar包含了commons-collections.jar/commons-lang.jar/oro-2.0.8.jar。
			velocity-1.6.jar没有包含这些文件，如果在单独使用Velocity的话，可以使用velocity-1.6-dep.jar,如果结合其他框架使用，可以使用velocity-1.6.jar
			参考velocity项目，实际应用参考：Digest_wx项目的保存标签MarkVO案例。
			
		3、SSI包含页面技术，比jsp的include包含要性能好。
		4、全文检索compass技术。
			商品搜索：
			1、数据库的like模糊查询
			2、全文检索Lucene
			使用全文检索Lucene的优点：
			a、性能：
			在数据量比较大，查询字段比较多的话，采用数据库like SQL语句模糊查询性能较差。
			采用全文检索Lucene性能较好。
			b、采用Lucene进行搜索，搜索到的结果相关度比较高，而且会把匹配度高的记录排在最前面，而
			数据库like SQL只会查询包含关键字的记录，结果的相关度不高，而且不会把匹配度高的记录排在最前面。
			c、采用Lucene进行搜索，能把想要突出的内容高亮显示，数据库like做不到。
			
			如何使用Lucene:
				1、建立索引 需要对内容进行分词，把分词建立索引
				a、一元分词 把一个字作为词。
				b、二元分词 把两个字作为词。
				c、字典分词（最好，目前用得比较多的分词，paoding解牛分词器）
			采用compass，不直接使用Lucene的api来实现全文检索，而是使用compass框架，因为
			compass框架以面向对象的思想封装了全文检索，compass的底层依赖Lucene，compass是
			基于Lucene面向对象的方式去操作搜索索引。类似于hibernate封装jdbc。
			我们把通过面向对象的方式去操作关系数据库的框架，叫做ORM框架，比如：hibernate、jdo、ibaties
			我们把通过面向对象的方式去操作搜索索引的框架，叫做（Object search engine mapping）简称：OSEM框架/产品，比如：compass产品。
			
		实现hibernate框架来开发项目：
		1、设计实体bean，并且完成实体的映射元数据（hbm.xml/@Entity）
		2、利用hibernate的api完成对实体bean的增删改查操作（crud操作）。
	
		compass框架添加索引实体到索引document文档中，类似hibernate的相关api操作。
		
		compass可以实现增量索引（企业很看重这个，有技术难度）。
		
		//性能优化：问题
		对于大型的门户系统（>10w人/天）是一定要性能优化的，小系统(OA/ERP/CEM同时在线200人左右是否优化无所谓)。
		问题：频繁的访问数据库，会出现数据库瓶颈问题，每个数据库最大的连接数（socket）为2000个，如果在某个短暂的时间里
		1万个人访问了产品页面，跟数据库发生1位次交互，数据库的处理能力是有限的，在这段时间里他只能处理2000个，还有8000个链接处理等待状态。
		在他等待的超时时间内30秒，假设数据库在这30秒还能处理5000个链接；
		前面2000个链接，很快能看到页面，后面的5000个链接会慢很多，最后面的3000个链接提示超时/服务器抛异常。
		软件解决方案：
		1、页面静态化（事先做好静态页面，这时用户在短时间内1万个用户去访问页面，跟数据库交互为0次，性能提示1万倍），页面的更新频率不高，页面显示时组合方式比较少，可以考虑页面静态化。
		2、缓存技术（允许有少量的数据库操作）
			a、页面缓存（view页面层，缓存html代码）缺点：不能做大实时更新，优点：比二级缓存性能更高。
			      在缓存的有效期内，页面不会发生改变，比如你在后台作了修改，在页面缓存的有效期内，页面不会发生改变，
			      如果想实时更新，你可以更新产品后，立刻清除相关的缓存，并重新刷新该页面，让页面重新缓存。
			b、二级缓存（Mode业务层，缓存domain对象），优点：能实时更新，性能不及页面缓存。
			
			页面缓存：
			1、全局缓存，缓存整个页面的HTML代码。参考Digest_wx项目的MarkVo标签的列表。
			2、局部缓存，缓存页面中的某个区域的HTML代码。参考oscache项目
			
		3、数据源（连接池里存放一些链接对象，减少数据库创建链接的次数），若不使用数据源，每次访问都要跟数据库建立链接（socket），数据源的连接池会放一些已经链接好的链接对象，服务完后仍然存放到连接池里，
		       下次有访问时，又从数据库连接池里获取已经链接好的链接，而不是重新去创建新的链接对象。
		4、SSI技术来实现页面的包含，对性能有所改善。
		SSI:service side include 服务器端包含技术
		       
	}
	
	public void A某个页面被浏览了多少次() {
		1、页面被加载时，通过ajax发送请求进行统计。
		2、可以利用加载图片时请求的路径，来请求action进行统计 <img src="xxx.action" width=0 height=0 />
	}
	
	public void A内存java虚拟机内存分析() {
		-Xms128m JVM初始分配的堆内存
		-Xmx512m JVM最大允许分配的堆内存，按需分配
		-XX:PermSize=64M JVM初始分配的非堆内存
		-XX:MaxPermSize=128M JVM最大允许分配的非堆内存，按需分配
		
		Java 虚拟机具有一个堆，堆（heap）是运行时数据区域，所有类实例和数组的内存均从此处分配。堆是在 Java 虚拟机启动时创建的。
		在JVM中堆之外的内存称为非堆内存(Non-heap memory)
		JVM主要管理两种类型的内存：堆和非堆。简单来说堆就是Java代码可及的内存，是留给开发人员使用的；非堆就是JVM留给自己用的，
		方法区、JVM内部处理或优化所需的内存(如JIT编译后的代码缓存)、
		每个类结构(如运行时常数池、字段和方法数据)以及方法和构造方法的代码都在非堆内存中。 
		
		堆内存分配：
		JVM初始分配的堆内存由-Xms指定，默认是物理内存的1/64；JVM最大分配的堆内存由-Xmx指定，默认是物理内存的1/4。
		默认空余堆内存小于40%时，JVM就会增大堆直到-Xmx的最大限制；
		空余堆内存大于70%时，JVM会减少堆直到-Xms的最小限制。因此服务器一般设置-Xms、-Xmx 相等以避免在每次GC 后调整堆的大小。
		说明：如果-Xmx 不指定或者指定偏小，应用可能会导致java.lang.OutOfMemory错误，此错误来自JVM，不是Throwable的，
		无法用try...catch捕捉。 
		
		非堆内存分配：
		JVM使用-XX:PermSize设置非堆内存初始值，默认是物理内存的1/64；由XX:MaxPermSize设置最大非堆内存的大小，默认是物理内存的1/4。
		
		PermGen space的全称是Permanent Generation space，是指内存的永久保存区域。
		XX:MaxPermSize设置过小会导致java.lang.OutOfMemoryError: PermGen space 就是内存益出。非堆内存溢出。 
		（1）这一部分内存用于存放Class和Meta的信息，Class在被 Load的时候被放入PermGen space区域，
			它和存放Instance的Heap区域不同。 
		（2）GC(Garbage Collection)不会在主程序运行期对PermGen space进行清理，
			所以如果你的APP会LOAD很多CLASS 的话,就很可能出现PermGen space错误。
			这种错误常见在web服务器对JSP进行pre compile的时候。  
		如果你的WEB APP下都用了大量的第三方jar，其大小超过了服务器jvm默认的大小，那么就会产生PermGen space内存益出问题了。
		解决方法： 设置MaxPermSize大小，增大non-heap堆内存。
		
		
		
	}
	
	public void A内存溢出解决方案() {
		问题：Java 内存溢出（java.lang.OutOfMemoryError）：
		1、jvm虚拟机内存过小。
		2、程序写得不严密，垃圾对象过多，未及时释放。（集合类中有对对象的引用，使用完后未清空，使得JVM不能回收）
		3、内存中加载的数据量过于庞大，如一次从数据库取出过多数据；
		4、代码中存在死循环或循环产生过多重复的对象实体；
		5、使用的第三方软件中的BUG；启动参数内存值设定的过小；
		

		解决java.lang.OutOfMemoryError的方法有如下几种：
		一、增加jvm的内存大小。
			方法有：对tomcat容器，可以在启动时对jvm设置内存限度。对tomcat，可以在catalina.bat中添加：
					set CATALINA_OPTS=-Xms128M -Xmx256M
					set JAVA_OPTS=-Xms128M -Xmx256M
					
		二、 优化程序，释放垃圾。
		主要包括避免死循环，应该及时释放种资源：内存, 数据库的各种连接，防止一次载入太多的数据。
		导致java.lang.OutOfMemoryError的根本原因是程序不健壮。因此，从根本上解决Java内存溢出的唯一方法就是修改程序。
		及时地释放没用的对象，释放内存空间。 遇到该错误的时候要仔细检查程序。
		
		需要重点排查以下几点：
		检查代码中是否有死循环或递归调用。
		检查是否有大循环重复产生新对象实体。
		检查对数据库查询中，是否有一次获得全部数据的查询。
		一般来说，如果一次取十万条记录到内存，就可能引起内存溢出。
		这个问题比较隐蔽，在上线前，数据库中数据较少，不容易出问题，上线后，数据库中数据多了，一次查询就有可能引起内存溢出。因此对于数据库查询尽量采用分页的方式查询。
		检查List、MAP等集合对象是否有使用完后，未清除的问题。List、MAP等集合对象会始终存有对对象的引用，使得这些对象不能被GC回收。
	
		
	
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
		cank:http://blog.csdn.net/py_xin/article/details/50564339
		
		项目案例：参考Digest_wx_apache_integrate_tomcat项目。
	}
	
	public void A用Collection容器分页展示某个试图() {
		/**
		 * list容器中根据page的值实现分页,list已经查询出了所有的数据对象，通过分页显示，展示list中某个视图。
		 * 泛型的应用
		 * @param <T>
		 * List<T> java.util.List.subList(int fromIndex, int toIndex)
		 * fromIndex从哪个下标开始，toIndex到哪个下标 fromIndex <= x < toIndex
		 */
		public static <T> List<T> getPageList(List<T> list, Page page) {
			List<T> newList = null;
			if (CollectionUtils.isNotEmpty(list)) {// 先确认list是否为空
				newList = new ArrayList<T>();
				if (page.getPageNo()*page.getPageSize()>list.size()) {//说明没有下一页了。
					/**极端的情况，比如pageNo=1，list.size=9,但是pageSize=15*/
					if (page.getPageNo() > 1) {// 说明不是首页
						int fromIndex = (page.getPageNo()-1)*page.getPageSize();
						int toIndex = list.size();
						newList.addAll(list.subList(fromIndex,toIndex));
					} else {// 如果只有一页的情况下，即pageNo=1那就拿取所有的数据。
						newList.addAll(list.subList(0, list.size()));
					}
				} else {// 说明当前还有下一页
					int fromIndex = (page.getPageNo()-1)*page.getPageSize();
					int toIndex = (page.getPageNo()*page.getPageSize());
					newList.addAll(list.subList(fromIndex,toIndex));
				}
			}
			return newList;
		}
	}

	public void B前台Web层框架() {
		Struts2或SpringMVC或JSF2.0等作为Web层框架，大体功能作用：
		1、获取表单内容，实现参数的传递和接收。
		2、把不同的请求交由合适的Controller控制器来处理。
		3、在控制器中调用业务接口，实现分层解耦。
		4、将业务接口返回的结果包装起来发送到指定页面视图，并在页面视图展现。
		5、做一些简单数据验证或国际化等工作。
	}
	
	public void B后台ORM框架() {
		接口：JPA。
		JPA（java persistence api）可以用Hibernate、myBites、jdo等持久化层实现
		主要通过EntityManager对象来完成增删改查等操作。
		
		实现JPA的产品：
		Hibernate、ibaties（myBaties的前身）、myBaties、jdo、ejb entiy bean等持久层框架，
		大体功能作用：
		都是与数据库打交道的框架，封装了底层的JDBC等操作，大大简化了使用JDBC操作数据库的复杂性和冗余性。
	}
	
	public void B三大框架的大致作用() {
		Spring框架，大体功能作用：
		1、spring充当了容器管理者的角色，实现分层解耦。
		2、spring负责事务管理。
		3、提供了依赖注入功能IOC、提供AOP面向方面编程。
		/***
		 * Spring的2个核心东西 
		 * 1、IOC控制反转或者DI依赖注入（1、实例化具体的bean、2、动态装配）
		 * 2、AOP切面编程（安全检查、事务管理、权限管理等）filter/interceptor都是典型的AOP编程
		 */
		B后台ORM框架();
		B前台Web层框架();
	}
	
	/***
	 * 对象的相关属性，会被挨着排写成Json格式的字符串。
	 */
	public void C把对象转换成JSon格式() {
		jackon-mapper-as-1.8.5.jar
		org.codehaus.jackson.map.ObjectMapper
		1、
		ObjectMapper mapper = new ObjectMapper();
		Object result = new JsonResult();
		result.setMessage("message是JsonResult类的属性，我是value");
		String jsonString = mapper.writeValueAsString(result);
		
		2、
		Person userDTO = new Person();  
        userDTO.setName("zhangshang");  
        userDTO.setSex('男');
        /** 把一个普通对象，转换成Json对象 */
        JSONObject jsonParam = JSONObject.fromObject(userDTO);  
        
        JSONObject userStr = (JSONObject) responseJSONObject.get("userDTO");  
        /** Json对象转换成普通对象 */
        userDTO = (Person) JSONObject.toBean(userStr, Person.class);  
        
        3、//把一个Object对象封装成了Json对象
    	public static JSONObject success(Object data) {
    		JSONObject map = new JSONObject();
    		map.put("code", 0);
    		map.put("data", data);
    		return map;
    	}
	}
	
	public void C常用的工具包() {
		1、commons-lang.jar的StringUtils。
		StringUtils.isNotEmpty(keywords);//判断是否为空，空字符不认为为空
		StringUtils.isNotBlank(keywords);//判断是否为空白，null/空字符都是空白
		StringUtils.isNumeric(params[0]);//判断某个字符串是否为数字
		StringUtils.split("asc,desc", ',');//以,为分隔符，分隔字符串成字符串数组。
		StringUtils.join(arry);//把一个数组对象，join到字符串中。
		
		2、commons-io.jar的org.apache.commons.io.IOUtils。
		
		3、commons-collections.jar的org.apache.commons.collections.CollectionUtils。
		CollectionUtils.isNotEmpty(list);//判断某个list容器是否为空，包括null。
		CollectionUtils.size(list);//获取某个list容器的大小。
		
	}
	
	public void C把硬盘上的文件加载到程序内存中() {
		参考：com.tentinet.app.util.OMSSecurityFilter的init()方法。
	}
	public void C获取项目的真实路径() {
		程序的入口是：servlet程序，
		1、可以直接在web.xml里配置servlet，通过servlet-mapping来匹配请求。
		2、可以通过web框架，比如Struts、SpringMVC等，通过action、Controller层的HTTPServletRequest对象，
		和HTTPServletResponse对象来完成请求。
		获取项目的真实路径：
		比如：D:\workspace\.metadata\.plugins\org.eclipse.wst.server.core\tmp1\wtpwebapps\Digest_wx
		1、HttpServletRequest对象request.getSession().getServletContext().getRealPath("/");
		2、在filter中调用，filterConfig.getServletContext().getRealPath("/");
	}
	
	public void C获取项目的请求路径() {
		HttpServletRequest是基于HTTP协议的ServletRequest对象。
		1、HttpServletRequest request;
		1、HttpServletRequest request=(HttpServletRequest)ServletRequest;
		/**
		 * url=http://localhost:8080/Digest_wx
		 */
		String url = request.getScheme()+"://"
				+request.getServerName()+":"+request.getServerPort()
				+request.getContextPath();
		System.out.println("url="+url);
		/**
		 * 获取uri=/Digest_wx/information/upload.do
		 */
		System.out.println("获取uri="+request.getRequestURI());
		/**
		 * 获取参数：dir=image
		 */
		System.out.println("获取参数："+request.getQueryString());
		/**
		 * 获取全路径=http://localhost:8080/Digest_wx/information/upload.do
		 */
		System.out.println("获取全路径="+request.getRequestURL());
	}
	
	public void C文件上传1() {
		在kindeditor编辑器中的文件上传：
		HttpServletRequest request。
		/**
		 * multipartRequest对象能获取到文件等内容
		 */
		MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
		/**
		 * 获得用户从Editor上传的文件：
		 * imgFile参考如下js里的配置，
		 * image.js/media.js:
		 * filePostName = K.undef(self.filePostName, 'imgFile'),
		 */
		MultipartFile file = multipartRequest.getFile("imgFile");
		// 获得文件名：
		String fileName = file.getOriginalFilename();
		//获得文件的后缀名
		if (fileName.lastIndexOf(".") >= 0) {
			String img_name = fileName.substring(fileName.lastIndexOf("."));		
		}
		//后缀保持不变重命名，生成随机id的文件名，避免用户上传的是中文的文件名文件
		fileName = UUID.randomUUID().toString()+img_name;
		try {
			InputStream input = file.getInputStream();
			String imagePath = ConfigUtil.getValue("baseDir")+ConfigUtil.getValue("imageDir");// 图片路径
			System.out.println("imagePath="+imagePath+fileName);
			//把上传的文件写入到指定路径的硬盘位置
			FileOutputStream fos = new FileOutputStream(imagePath + fileName);
			/**
			 * byte[]是一个字节数组，每次读取1024个字节存放到该字节数组里，然后再写入到输出管道。
			 */
			byte[] buffer = new byte[1024];
			int len = 0;
			while ((len = input.read(buffer)) != -1) {
				fos.write(buffer, 0, len);
			}
		} catch (Exception ex) {
			System.out.println(ex);
		}
	}
	
	public void C文件上传2() {
		/**
		 * 文件上传，使用uploadify插件，上传文件
		 */
		参考：前台：information/information_ChangeTable.html
		后台：com.tentinet.app.util.UploadServlet
		实质：上传文件，下载文件，道理都是一样的，都是把目标文件读取到内存，然后通过输出管道写入到指定的路径。
	}
	public void C文件下载() {
		参考：reportForms/goldSend/gold_send_list.html的下载
		@RequestMapping(value = "/Report/downLoad.do")里处理数据，把数据写入到Excle，
		然后通过回调函数，请求下载的sevlet。com.tentinet.app.util.FileDownServlet。
		
	}
	public void D字符串格式化() {
		1、一个字符占位符，可以直接用String的format方法
		String s = "http://localhost/digest/ha.do?name=%s";
		String new_s = String.format(s,"jobs1127");
		new_s = "http://localhost/digest/ha.do?name=jobs1127";
		
		2、多个占位符时
		String url = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid={0}&secret={1}";
		可以使用MessageFormat.format(url, appid,secret);
	}
	public void D缓存相关() {
		1、自己写的查询缓存：
		@SuppressWarnings("unchecked")
		@RequestMapping(value = "/information/queryInformation.do")
		public void queryInformation(HttpServletRequest request,
				HttpServletResponse response, String title, String keywords,
				String status, String openid, String best_flag, Page page) {
			/***
			 * page对象中有pageSize、pageNo，故可以通过page对象来接收前台页面传递过来的参数。
			 * page接收前台页面的pageSize、pageNo参数
			 */
			HttpSession session = request.getSession();
			Map<String, Object> params = new HashMap<String, Object>();
			if (StringUtils.isNotEmpty(title)) {
				params.put("title", "%" + title + "%");
			}
			if (StringUtils.isNotEmpty(keywords)) {
				params.put("keywords", "%" + keywords + "%");
			}
			if (StringUtils.isNotEmpty(status) && !StringUtils.equals("0", status)) {
				params.put("status", status);
			}
			if (StringUtils.isNotEmpty(status) && StringUtils.equals("0", status)) {
				params.put("status_34", "status_34");
			}
			if (StringUtils.isNotEmpty(openid)) {
				params.put("openid", openid);
			}
			if (StringUtils.isNotEmpty(best_flag) && !"0".equals(best_flag)) {
				params.put("best_flag", best_flag);
			}
			
			/***
			 * 通过session保存第一次请求的参数字符串，然后其他的请求，获得的参数字符串都会与该session里的字符串比较，
			 * 如果相同则表示查询条件没变化，则无需去数据库拿取数据，而是直接拿取第一次请求获得的结果（从session里拿取）。
			 */
			/**
			 * StringBuffer线程安全的
			 */
			StringBuffer sb = new StringBuffer();
			
			for(String key:params.keySet()) {
				sb.append(key).append(params.get(key));
			}
			System.out.println("sb="+sb);
			List<InformationDto> list = null;
			List<InformationDto> newList = null;
			Integer count = 0;
			if(session.getAttribute("sb")!=null&&session.getAttribute("sb").toString().equals(sb.toString())) {
				
			} else {
				session.setAttribute("sb",sb.toString());
				/**
				 * 分页相关设置：
				 * 一次性查询出所有的对象，存放到collection容器中，然后在从容器中去拿取分页相关的数据。
				 * 前台通过js组件封装分页，每次点击分页页码时，仍然要调用该queryInformation.do方法，在访问数据库，
				 * 把所有的数据查询出来，并存放到list容器中，再通过分页相关属性，得到分页相关的数据视图。
				 */
				/**查询出一共有多少条记录*/
				count = informationService.queryInformationCount(params);
				session.setAttribute("information-count", count);
				/**查询出符合条件的所有对象，并存放到List容器里*/
				list = informationService.queryInformationInfos(params);
				session.setAttribute("information-list", list);
			}
			
			page.setTotalCount((Integer)(session.getAttribute("information-count")));
			/**从存放所有数据的List容器中，分页显示*/
			newList = getPageList((List<InformationDto>)(session.getAttribute("information-list")), page);
			
			System.out.println("分页："+page.getPageNo());
			//分页的js,参考page.js
			
			JSONObject oj = new JSONObject();
			oj.put("data", newList);
			oj.put("total", page.getTotalCount());
			writeResponseByJson(request, response, oj);
		}
	}
	
	
	
	public void 获取URL请求参数对应的值() {
		httpService.js里的方法:getURLParams(paras)/getQueryString(name)。
	}
	
	public void 去掉字符左右的空格() {
		/***
		 * 重写String类的trim()方法，参考httpService.js里trim()方法
		 */
		String.prototype.trim = function() {  
		    return this.replace(/(^\s*)|(\s*$)/g, "");  
		}; 
	}
	
	
	public void 随机验证码生成(){
		web.xml配置servlet，页面通过<img src="imgcode"/>标签去请求该servlet来生成。
				src会去请求服务器加载图片文件。
	}
	public void log日志(){
		简单的说，就是配合log的等级过滤输出
		比如，你在开发的时候，要验证一个方法有没有被调用到，为了方便调试，通常会在这个方法开始的时候加一些system.out。但是项目真正发布的时候这些代码通常是要移除掉的，所以通常更建议用logger来记录
		所以你可能会加logger.debug。 为什么是debug而不是info error或者其他呢？因为通常项目发布的时候都会把日志等级设置为error 或者info之类的等级，在这两个等级下debug的内容是输出不了的，所以就可以做到不需要修改代码就不会输出你只有在调试的时候才需要输出的内容
		各个等级都是有它的含义的，虽然在代码写的时候你用debug info error都是可以，但是为了方便管理，只有调试的时候才用到日志会用debug，一些信息类的日志记录通常会用info（比如你想看一天有几个用户登录），一些错误的，或者异常信息会用error，比如某个时刻数据库连接出了问题，如果分析日志，直接搜索error开头的就能直接定位到了
		slf4j+logback:相关jar包：slf4j-api-1.6.1.jar,log4j-over-slf4j-16.1.jar,logback-classic-0.9.27.jar,logback-core-0.9.27.jar
		
		接口：slf4j-api/commons-logging-api/jboss-logging.api
		实现：log4j/logback/commons-logging-impl
		slf4j到实现的适配器：slf4j-jdk4/slf4j-jcl/log4j-over-slf4j
		
		优势：转移到logback的理由 
	    slf4j支持参数化的logger.error("帐号ID：{}不存在", userId);
		告别了if(logger.isDebugEnable()) 时代。 
	   	另外logback的整体性能比log4j也较佳，
	   	hibernate等项目已经采用了slf4j:"某些关键操作，比如判定是否记录一条日志语句的操作，" +
	   			"其性能得到了显著的提高。这个操作在LOGBack中需要3纳秒，而在Log4J中则需要30纳秒。 " +
	   			"logback创建记录器（logger）的速度也更快：13毫秒，而在Log4J中需要23毫秒。" +
	   			"更重要的是，它获取已存在的记录器只需94纳秒，而 Log4J需要2234纳秒，时间减少到了1/23。" 
		logback 分为两种设置： 
		1. 输出到控制台 STDOUT 
		2. 输出到文件 FILE
		logback.xml配置（下面的配置同时配置输出到文件和输出到控制台）
		<?xml version="1.0" encoding="UTF-8" ?>
		<configuration scan="true" scanPeriod="3 seconds">
		    <!--设置日志输出为控制台-->
		    <appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
		        <encoder>
		            <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} %-5level [%X{userId}] [%X{requestId}] %logger - %msg%n</pattern>
		        </encoder>
		    </appender>

		    <!--设置日志输出为文件-->
		    <appender name="FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
		        <File>logFile.log</File>
		        <rollingPolicy  class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
		            <FileNamePattern>logFile.%d{yyyy-MM-dd_HH-mm}.log.zip</FileNamePattern>
		        </rollingPolicy>
		        <layout class="ch.qos.logback.classic.PatternLayout">
		            <Pattern>%d{HH:mm:ss,SSS} [%thread] %-5level %logger{32} - %msg%n</Pattern>
		        </layout>
		    </appender>
		    <root>
		        <level value="DEBUG"/>
		        <appender-ref ref="STDOUT"/>
		        <appender-ref ref="FILE"/>
		    </root>
		</configuration>
		参考：http://blog.csdn.net/conquer0715/article/details/9365899
	}
	
	public void html通过id获得一个html元素() {
		比如HTML标签：<image id="randomImage"/>
		
		1、通过HTML的方式拿取：
		document.getElementById('randomImage');
		
		2、通过jQuery的方式拿取，必须引入jQuery相关的js文件
		$("#randomImage");
		var username = $("#username").val();
		
		通过id拿取到了某个元素，或通过参数传递拿取到了某个元素，若何获取该元素的属性：
		<a href='#' style='margin-right:3px;width=4%'"
				+" class='cz' onclick='openwin(this,1)' information_id='"
				+ vJson.data[i].information_id
				+ "'>编辑</a>";
		openwin(_this,index) {
			var information_id = $(_this).attr("information_id");
		} 		
		
	}
	
	public void html常用方法() {
		1、键盘按下触发方法
		onkeydown="keyDown()";
		比如：<body onkeydown="keyDown()" >在整个body标签里，如果有监听到键盘按下，则触发该方法。
				
		2、点击某个元素触发方法
		onclick="login()"
		<input onclick="login()" type="button" class="btn"/>
		点击触发login()方法
		
		3、页面加载后触发方法
		onload="init()";
		比如：<body onload="init()" >在整个body标签被加载完后，则触发该方法。
	}
	
	public void html页面文字过长时换行() {
		<!--word-break:break-all;
		文字是否换行，break-all:有撑不到的都换行，word-break:keep-all:保持原样，不换行;
		word-wrap：文字包裹
		语法：
		word-wrap: normal|break-word;
		normal	只在允许的断字点换行（浏览器保持默认处理）。
		break-word	在长单词或 URL 地址内部进行换行。
		-->
		<table id="table2"
		style="width:100%;word-break:break-all; word-wrap:break-word;">
		</table>
	}
	public void html页面滚动条的相关设置() {
		/**
		 * overflow: scroll; 允许有滚动条，overflow-x: hidden x轴坐标隐藏
		 * overflow-y:隐藏y轴，overflow：hidden滚动条隐藏
		 */
		<body style='overflow: scroll; overflow-x: hidden'>
	}
	
	public void html清空某个id对应的元素并填充内容() {
		<select id="openid" name="openid" class="select"
			style="width:153px;">
		</select>
		$("#openid").empty();//清空openid对应的元素标签 也可以$("#openid").html("");
		/**
		 * 填充openid对应的元素标签,也可以
		 * var html = "<option value=''>全部</option>";
		 * $("#openid").html(html);
		 */
		$("#openid").append("<option value=''>全部</option>");
		for ( var i = 0; i < data1.length; i++) {
			$("#openid").append(
					"<option value='"+data1[i].dataItem+"'>"
							+ data1[i].dataValue + "</option>");
		}
	}
	public void html告诉浏览器不要缓存 () {
		<!-- 下面三句话，用于告诉浏览器不要缓存 -->
		<meta http-equiv="pragma" content="no-cache" />
		<meta http-equiv="cache-control" content="no-cache" />
		<meta http-equiv="expires" content="0" />
	}
	
	public void html的from表单元素们 () {
		/**action:提交后前往的URL；method：以什么方法提交：一般为get/post两种，默认为get
			1、post比get要安全
			2、浏览器地址栏对 提交的数据大小get有一定限制**/
		<form id="login" name="login" action="form_submit.html" method="post">
			//<!--文本域输入框--->
			用户名：<input type="text" name="username" value=""/><br>
			
			//<!--密码域--->
			密&nbsp;码：<input type="password" name="password" value=""/>
			<br/>
			
			//<!--复选框 ，使用label可以点击文字选中复选框--->
			爱&nbsp;好：
			<input id="hobby_lq" type="checkbox" name="hobby" value="langqiu"/> <label for="hobby_lq">篮球</label>、
			<input type="checkbox" name="hobby" value="lvmaoqiu"/>羽毛球、
			<input type="checkbox" name="hobby" value="pingpangqiu"/>乒乓球
			<br/>
			
			//<!--单选框--->
			性别：
			<input type="radio" name="sex" value="man"/>男、
			<input type="radio" name="sex" value="woman"/>女
			
			//<!--隐藏域--->
			<input type="hidden" name="id" value="1127"/>
			<br/>
			
			//<!--下拉框、下拉列表 单选--->
			下拉框单选水果：
			<select name="fruit">
				<option value="apple">苹果</option>
				<option value="pear">香梨</option>
				<option value="banala">香蕉</option>
				<option value="peach">桃子</option>
			</select>
			<br/><br/>
			
			//<!--下拉框、下拉列表 多选--->
			下拉框多选水果（按住Ctrl健多选）：
			<select name="fruits" multiple="multiple" size="2">
				<option value="apple">苹果</option>
				<option value="pear">香梨</option>
				<option value="banala">香蕉</option>
				<option value="peach">桃子</option>
			</select>
			<br/>
			
			//<!--文本域，多行--->
			文本域，多行:
			<textarea name="mytext" rows="4" cols="30">
			
			</textarea>
			<br/>
			
			//<!--上传文件--->
			上传文件：<input type="file"  name="myfile"/>
			
			<br/><br/><br/>
			
			//<!--提交和重置按钮--->
			<input type="submit" value="提交"/>
			<input type="reset" value="重置"/>
			<br><br>
			
			//<!--图片类型的提交按钮--->
			<input type="image" src="login.png" />
		</form>
	}
	
	public void js页面加载后立马执行的方法() {
		1、引入了jquery后可以：$(function(){alert('hh');});
		2、
		$(document).ready(function() {
			alert('hh');
		});
	}
	
	public void js页面某个id元素被点击后触发方法() {
		<button id="send"/>
		$('#send').click(function(){});
	}
	
	public void js页面jquery的ajax请求() {
		$.ajax({
			//参考httpService.js的sendRequest()方法。
		});
		
		var url = pageContextPath + "/information/queryInformation.do";
		var params = $("#informationForm").serialize();
		sendRequest(
				url,
				params,
				function(jsonData) {
					for ( var i = 0; i < jsonData.data.length; i++) {
						//data是请求url的controller里的存放着数据的集合
					}
				});
	}
	
	public void js页面checkbox全选和全不选() {
		<input type='checkbox' id='checktop' onclick='checkAll(this)'/>
		<input type='checkbox' name='checks' onclick='doublecheck()' />
		<input type='checkbox' name='checks' onclick='doublecheck()' />
		<input type='checkbox' name='checks' onclick='doublecheck()' />
		
		/**
		 * 全选/全不选 check box，全选当前页的check box
		 * node就是当前点击的check box，是否checked：true表示选中，false表示没选中。
		 */
		function checkAll(node) {
			/***
			 * 根据元素名拿到所有的元素，是一个数组
			 */
			var collNodes = document.getElementsByName("checks");
			//alert(collNodes.length);
			/**
			 * 循环迭代该数组，把每个check box都拿出来，修改他们的checked属性，和当前点击的check box 属性一致即可。
			 * 当前点击的check box选中，则其他的所有check box就选中。
			 */
			for ( var i = 0; i < collNodes.length; i++) {
				collNodes[i].checked = node.checked;
			}
			//alert(node.checked);
		}
		
		/***
		 * 当全选后，再次去点击check box
		 */
		function doublecheck() {
			var collNodes = document.getElementsByName("checks");
			var b = 0;
			var tops = document.getElementById("checktop");
			for (var i = 0; i < collNodes.length; i++) {
				if (collNodes[i].checked) {
					tops.checked = false;
				} else {
					b++;
				}
			}
			if (b == 0) {
				tops.checked = collNodes[0].checked;
			}
		}
	}
	
	public void js常用插件() {
		-1、jquery插件，很多插件都是基于该插件。
		1、jQueryAlertDialogs插件，使用它可以用来替换JScript中的alert,confirm,prompt。
		2、jquery-ui-1.12.1插件，可以充分利用jQuery的UI设计。
		3、jquery-ztree插件，树状结果的展示，树状CheckBox等。
		4、uploadify插件，文件上传。
		5、kindeditor-4.1.10插件，文件，图片，视频的后台编辑器插件。
	}
	
	public void js常用方法() {
		1、trim()
		/**
		 * trim函数，重写了String的trim(),比如p="  aa  ",p.trim()后可以去掉aa左右前后的空格字符。
		 * 空格开头或者空格结尾
		 * ^是开始
		 * \s是空白
		 * *表示0个或多个
		 * |是或者
		 * $是结尾
		 * g表示全局，g:执行全局匹配,而不是找到第一个匹配就停止。
		 * 匹配首尾空格的正则表达式：(^\s*)|(\s*$)
		 */
		String.prototype.trim = function() {  
			return this.replace(/(^\s*)|(\s*$)/g, "");  
		}; 
		
		2、toFixed(2)四舍五入保留2位/toFixed(1)四舍五入保留1位
		vJson.data[i].award_money.toFixed(2);
		
		3、jQuery根据id获取某个元素的值，并判断是否为空
		if ($("#title").val().trim() == "") {
			alert("资讯名称为空");
			$("#title").focus();//光标定位到某个id元素。
			return;
		}
		4、根据元素的属性，获得该元素的值
		var information_id = $(_this).attr("information_id");
		
		5、js 中判断某个元素是否存在于某个 js 数组中
		//方法定义
		Array.prototype.in_array=function(e){
		var r=new RegExp(','+e+',');
		return (r.test(','+this.join(this.S)+','));};
		//使用方法：
		var arr=new Array(['b',2,'a',4]);
		boolean exists=arr.in_array('b');//判断'b'字符是否存在于 arr 数组中，存在返回true 否则false，此处将返回true
		
		6、js数组中，移除下标对应的元素
		var ary = [1,2,3,4]; 
		ary.splice(0,ary.length);//清空数组 ，从下标为0开始移除 ary.length个元素
		ary.splice(0,1);//从下标为0开始移除 1个元素
		
		7、
		//转换成float类型
		var price= parseFloat(_this.attributes.price.value);
	}
	public void js后台KindEditor编辑器的使用() {
		1、下载kindeditor-4.1.10，并拷贝到项目中
		2、引入：
		<!-- kindeditor编辑器相关js start-->
		<script charset="utf-8" src="../js/lib/kindeditor-4.1.10/kindeditor.js"></script>
		<script charset="utf-8" src="../js/lib/kindeditor-4.1.10/lang/zh_CN.js"></script>
		<script charset="utf-8" src="../js/lib/kindeditor-4.1.10/plugins/media/media.js"></script>
		<link rel="stylesheet" href="../js/lib/kindeditor-4.1.10/themes/default/default.css" type="text/css"/>
		<link rel="stylesheet" href="../js/lib/kindeditor-4.1.10/plugins/code/prettify.css" type="text/css"/>
		<script charset="utf-8" src="../js/lib/kindeditor-4.1.10/kindeditor-all.js" type="text/javascript"></script>
		<script charset="utf-8" src="../js/lib/kindeditor-4.1.10/plugins/code/prettify.js" type="text/javascript"></script>
		<!-- kindeditor编辑器相关js end-->
		3、使用：
		/**
		编辑器 参考：http://www.sucaihuo.com/js/123.html
		KindEditor 是一套开源的在线HTML编辑器，主要用于让用户在网站上获得所见即所得编辑效果，
		开发人员可以用 KindEditor 把传统的多行文本输入框(textarea)替换为可视化的富文本输入框。
		**/
		KindEditor.ready(function(K) {
			//#editor_id编辑器显示的<textarea id="editor_id"></textarea>对应的id元素
			$.editor = window.editor = K.create('#editor_id',
			{
				//上传文件时调用,指定上传文件的服务器端程序。
				uploadJson:pageContextPath + '/information/upload.do',
				//指定浏览远程图片的服务器端程序。
				//fileManagerJson:pageContextPath+'/information/resourcesManage.do',
				//true或false，true时显示浏览服务器图片功能。
				allowImageUpload:true,//允许上传图片
				//编辑器的宽度，可以设置px或%，比TEXTAREA输入框样式表宽度优先度高。
				width : '100%',
				//编辑器的高度，只能设置px，比TEXTAREA输入框样式表高度优先度高。
				height : '500px',
				//数据类型：Boolean 。true时过滤HTML代码，false时允许输入任何代码。
				//filterMode:false,
				//数据类型：Object。指定要保留的HTML标记和属性。哈希数组的key为HTML标签名，value为HTML属性数组
				//htmlTags:{ font : ['color', 'size', 'face', '.background-color'], span : ['style'], div : ['class', 'align', 'style'], table: ['class', 'border', 'cellspacing', 'cellpadding', 'width', 'height', 'align', 'style'], 'td,th': ['class', 'align', 'valign', 'width', 'height', 'colspan', 'rowspan', 'bgcolor', 'style'], a : ['class', 'href', 'target', 'name', 'style'], embed : ['src', 'width', 'height', 'type', 'loop', 'autostart', 'quality', 'style', 'align', 'allowscriptaccess', '/'], img : ['src', 'width', 'height', 'border', 'alt', 'title', 'align', 'style', '/'], hr : ['class', '/'], br : ['/'], 'p,ol,ul,li,blockquote,h1,h2,h3,h4,h5,h6' : ['align', 'style'], 'tbody,tr,strong,b,sub,sup,em,i,u,strike' : [] },
				//2或1或0，2时可以拖动改变宽度和高度，1时只能改变高度，0时不能拖动。
				resizeMode:2,
				//数据类型：Boolean。可视化模式或代码模式
				//wyswygMode:true,
				//afterUpload:function(){this.sync();},
				//afterBlur:function(){this.sync();},
				//items	配置编辑器的工具栏
				items : ['source', '|', 'fullscreen', 'undo', 'redo', 
				         'print', 'cut', 'copy', 'paste', 'plainpaste', 
				         'wordpaste', '|', 'justifyleft', 'justifycenter', 
				         'justifyright', 'justifyfull', 'insertorderedlist', 
				         'insertunorderedlist', 'indent', 'outdent', 'subscript', 
				         'superscript', '|', 'selectall', '-', 'title', 'fontname', 
				         'fontsize', '|', 'textcolor', 'bgcolor', 'bold', 'italic', 
				         'underline', 'strikethrough', 'removeformat', '|', 'image', 
				         'flash', 'media', 'advtable', 'hr', 'emoticons', 'link', 
				         'unlink', '|', 'about']
			});
		});
		
		<body>
		<textarea id="editor_id"></textarea>
		</body>
	}
	
	public void css常用属性() {
		//间距：
		margin: 0;//元素和元素之间的边距，元素相离 其他写法：margin: 30px 0 0 25px;
		margin-bottom: 20px;//距离下面元素的距离
		margin-right: 30px;//距离右边的元素的距离
		padding: 0;//单一元素里的边距，元素相交
		padding-top: 15px;//元素内上间距
		top: 222px;//距离上面
		left: 300px;//距离左边
	
		//宽高
		width: 100%;//宽度，还可以width:90px;
		height:100%;//高度，还可以height:90px;
		
		//背景相关
		background: url(../images/bg.gif);/*   ../ 表示根目录      */
		background-color: #043566;//背景颜色
		background-size: cover; /*background-size是css3中新增的属性,background-size的cover特定值会保持图像本身的宽高比例,将图片缩放到正好完全覆盖定义背景的区域。**/
		
		//文字
		font-size: 14px;//文字大小
		font-family: "宋体";//文字的字体
		color: #22508e;//文字颜色
		font-weight:bold;/**对文字的修饰，粗体显示*/
		line-height: 42px;//行高
		text-align: center;//文本居中
		font: 18px/40px Microsoft Yahei;//雅黑
	  
	
	
		/**除去滚动条，隐藏滚动条*/
		scroll:no;
		overflow: hidden;
	
		//位置
		position: absolute;/**位置为绝对*/
		float: right;//漂浮在右边
		float: left;//漂浮在左边
	
		//边框，不如table的边框
		border: 1px solid #f00;
		border-radius: 3px;//边框的菱角
		border: solid #e7e7e7 1px;//边框
	
	
		//在<a标签中常用，显示手指
		cursor: pointer;
		//在<a标签中常用，显示下划线
		text-decoration:underline;
		text-decoration:none;//不显示下划线
		
	}
	public void css常用方法() {
		1、鼠标经过时改变背景颜色
		html:
		<table id="table2"
			style="width:100%;word-break:break-all; word-wrap:break-word;">
		</table>
		
		css:
		#table2 tr:hover {
			background-color: #fffbbb;
		}
		
		2、把某个元素居中显示
		<center>
			<input onclick="login()" type="button" class="btn"/>
		</center>
		
		3、js的循环中换行tr、td
		sendRequest(pageContextPath + "/wechat/queryallmarks.do", null,
			function(jsonData) {
				var data = jsonData.list;
				$("#mark_table").html("");
				var str = "";
				if (data != null) {
					for ( var i = 0; i < data.length; i++) {
						if (i == 0) {
							str += "<tr>";
						}
						/*table 每三个td 换一行*/
						if (i > 0 && i % 3 == 0) {
							str += "</tr>";
							str += "<tr>";
						}
						str += "<td><li id=" + data[i].mark_code
								+ " onclick='selectMark(this)' class='' >"
								+ data[i].mark_name + "</li></td>";
						if (i == data.lenght) {
							str += "</tr>";
						}
					}
				}
				$("#mark_table").html(str);
			});
		
		4、js需改某个元素的样式
		<li id=" + data[i].mark_code
		+ " onclick='selectMark(this)' class='' >"
		+ data[i].mark_name + "</li>;
		
		//拿到某个元素的class 如果等于某个颜色就做某事
		if ($(_this).attr("class") == "aisorted") {
			$(_this).attr("class", "");
		} else {
			$(_this).attr("class", "aisorted");
		}
		
		5、标签里的样式循环
		var str_mark = "";
		//li标签的==aisorted的class循环迭代
		$("li.aisorted").each(function(i, own) {
			str_mark += $(this).attr('id') + ',';
		});
		
		6、ajax参数传递形式：
		//保存用户的标签，记录用户感谢的标签文章
		$.ajax({
			url : pageContextPath + "/wechat/savemarks.do",
			//直接通过{}包起来，json格式
			data : {
				"marks" : marks,
				"re_openid" : re_openid
			},
			type : "POST",
			success : function(data) {
				window.location.href = "ObtainSuc.jsp";
			}
		});
		//var params = $("#informationForm").serialize();
		data:params,
		//自写的参数+序列化的参数
		data:"pageSize=15&pageNo=" + pageNo + "&" + params,
	}
	
	public void js页面显示年月日星期() {
		/**
		 * js显示年月日 星期
		**/
		function writeDateInfo() {
			var day = "";
			var month = "";
			var ampm = "";
			var ampmhour = "";
			var myweekday = "";
			var year = "";
			mydate = new Date();
			myweekday = mydate.getDay();
			//alert(myweekday);
			mymonth = mydate.getMonth() + 1;
			myday = mydate.getDate();
			myyear = mydate.getYear();
			year = (myyear > 200) ? myyear : 1900 + myyear;
			if (myweekday == 0) {
				weekday = " 星期日";
			} else if (myweekday == 1) {
				weekday = " 星期一";
			} else if (myweekday == 2) {
				weekday = " 星期二";
			} else if (myweekday == 3) {
				weekday = " 星期三";
			} else if (myweekday == 4) {
				weekday = " 星期四";
			} else if (myweekday == 5) {
				weekday = " 星期五";
			} else if (myweekday == 6) {
				weekday = " 星期六";
			}
			$("#welTime").html(
					year + "年" + mymonth + "月" + myday + "日&nbsp;&nbsp;" + weekday);
		}
	}
	
	
	
	
	
	
}