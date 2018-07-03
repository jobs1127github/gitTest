/**
 * 1、什么产品? maven
 * 
 * 2、为什么用? 
 * Maven的出现，解决了开发过程jar包太多，版本太多，互相之间还可能存在冲突的难题。
 * 它可以对项目依赖的jar包进行管理，可以让你的项目保持基本的依赖，排除冗余jar包，
 * 并且可以让你非常轻松的对依赖的jar包进行版本升级。
 * 而这些仅仅是Maven最基本的功能，它可以在这基础上对项目进行清理、编译、测试、打包、发布等等构建项目的工作。
 * 
 * 3、怎么使用?  
 * 参考maven项目。
 * 
 * 4、有什么好处和心得? 
 * pom.xml配置文件里，通过配置<dependency>可以灵活的使用jar，maven也会自动帮你管理版本问题。
 * 没有的jar他会去网上下载。
 * 
 * @author jobs1127
 *
 */
public class maven{
	/**
	 * 参考：https://www.cnblogs.com/teach/p/5906425.html
	 */
	public void maven安装和eclipse集成(){
		maven作为一个项目构建工具，在开发的过程中很受欢迎，可以帮助管理项目中的包依赖问题，
		另外它的很多功能都极大的减少了开发的难度，下面来介绍maven的安装及与eclipse的集成。
		maven的官网地址为：http://maven.apache.org/，打开网页之后，看到最新的版本是3.3.9，从对3.3.9版本的介绍，可以知道3.3以上的版本需要JDK1.7的支持。
		由于maven需要JDK的支持，所以在安装maven之前请确保电脑上已经安装了JDK，且配置好了环境变量，具体的JDK的安装及配置可自行百度。
		安装jdk一定要注意安装的路径，配置环境变量：JAVA_HOME:jdk的安装路径，比如：D:\downloadfiles\jdk1.7
		
		maven安装
		1、把下载好的压缩包解压到一个路径，我这里解压的路径为：D:\apache-maven-3.5.2，
	    2、解压之后需要配置环境变量，新建MAVEN_HOME环境变量，值为：D:\apache-maven-3.5.2，
	                  然后向path环境变量值的最后添加如下内容：%maven_home%\bin;，
	                  注意在向path中添加值的时候不同的值需要以英文状态下分号隔开，且最后一个值也需要以分号结尾，点击确定完成环境变量的配置。
	    3、测试，在命令行中输入：mvn -v ，回车，如能看到版本信息，则证明maven安装成功。
	    maven安装完成之后可以单独使用，独立使用maven创建项目、构建工程等。这里不介绍其单独使用的方式，这里介绍它和eclipse集成的方法。
	    maven和eclipse集成，参考：https://www.cnblogs.com/teach/p/5906425.html
	    
	    new Maven project
	    catalog:Internal //catalog(目录)、Internal（内部）
	    maven构建java项目：maven-archetype-quickstart
	    maven构建java web项目：maven-archetype-webapp 
	    
	         创建maven项目完成后，此时会连接网络从网上下载需要的jar包，jar包存放的路径就是配置的本地仓库的路径，
	         我的在：D:\apache-maven-3.5.2\mvnrepository路径下，
	         在D:\apache-maven-3.5.2\mvnrepository\org\apache\maven\plugins文件夹下是maven插件所需的依赖插件。
	}
	/**
	 * 参考：http://blog.csdn.net/w12345_ww/article/details/52094756
	 */
	public void eclipse导入已存在的maven项目(){
		1、通过eclipse的import导入已经存在的maven项目，不需要选中某个maven项目，
		而是导入一个已经存在的maven项目，eclipse会新建一个maven项目并拷贝已经存在的maven项目。
		
		2、自己新建一个maven项目，把已经存在的maven项目的src、target、pom.xml拷贝并覆盖自己的。然后修改相关的包名。
	}
	maven打包项目：
	mvn package -DskipTests
	
}