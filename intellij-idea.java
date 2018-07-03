/**
 * 
 * @author jobs1127
 *
 */

public class IDEA{
	public void readme(){
		1、什么是IDEA?
		IntelliJ-idea是一款综合的Java编程环境，被许多开发人员和行业专家誉为市场上最好的IDE。
		
		2、为什么使用它?
		自动补全、智能提示、编码辅助、效率高。
		
		3、使用心得?
			1、配置好字体、编码，工程编码。
			2、弄清该工具的目录结构
			3、代码编辑辅助快捷键，模板代码
			
////////////////////////////////////////////////////////////////////////////////////
		appearance：外观 behavior：行为 frameworks 框架

		1、idea 破解：http://blog.csdn.net/zx110503/article/details/78734428

		2、解决idea卡顿以及基础配置：
			1、http://blog.csdn.net/u013068377/article/details/54316965
			2、https://www.iyunv.com/thread-348537-1-1.html 
	
		3、IDEA使用--字体、编码和基本设置：http://blog.csdn.net/frankcheng5143/article/details/50779149 

		4、intellij idea怎么设置eclipse快捷键 https://zhidao.baidu.com/question/1047344364252005939.html

		5、line templates 设置代码模板。比如syso

		6、tasks/mybatis/checkStyle插件
		
		7、outline 视图 http://blog.csdn.net/ideality_hunter/article/details/53331700
		
	}
	
	public void 下载安装和破解(){
		下载地址：http://www.jetbrains.com/idea/#chooseYourEdition
		参考1：https://www.zhihu.com/question/20450079
		参考2：http://blog.csdn.net/a910626/article/details/45314457
	}
	
	
	public void 简单的优化(){
		安装目录：D:\downloadfiles\IntelliJ-IDEA-2017.3.2\bin
		如果操作系统是64位的，修改idea64.exe.vmoptions，修改下内存。
		默认安装后在C:\Users\dell1\.IntelliJIdea2017.3这个目录缓存用户的数据包括个性化设置的缓存文件索引index
		若果重装系统后该文件会消失，最好把他放在D盘，目前是：拷贝.IntelliJIdea2017文件到D盘：D:\downloadfiles\.IntelliJIdea2017.3
		备份idea - 副本.properties 修改idea.properties 把路径修改成上面的路径。
		把不要的插件plugin不勾选
	}
	
	/***
	 * 该插件从本地硬盘安装，并破解 
	**/
	public void myBatis插件(){
		参考：http://blog.csdn.net/u011410529/article/details/54098067
		1、提供Mapper接口与配置文件中对应SQL的导航。
		2、编辑XML文件时自动补全。
		3、根据Mapper接口, 使用快捷键生成xml文件及SQL标签。
		快捷键: Option + Enter(Mac) | Alt + Enter(Windows)
		
		破解：
		Intellij 的 mybatis plugin 3.21 版本插件，下载文件后解压，会看到两个文件。
		将 MyBatis_plugin-3.21.zip 直接使用 Intellij 安装，
		再到 C:\Users\艳丽\.IntelliJIdea2017.3\config\plugins (可能目录不一样)目录下，
		在使用 iMybatis-3.21.jar 将 lib 目录中的同名包替换即可正常使用。
	}
	
	
	
	public void 快捷键(){
		ctrl+"+"展开当前方法/ctrl+"-" 收缩当前方法 
		
		ctrl+alt+t：选中代码后按此快捷键，弹出try catch等选项
		
		F2：开方法的返回值类型
		
		F3：进入方法
		
	}
	
}