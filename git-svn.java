/****
 * 
 * @author jobs1127
 *
 * git概念、原理、使用
 * https://blog.csdn.net/zl1zl2zl3/article/details/52637737
 * 
 * Egit：（Git Eclipse Plugin）
 * https://blog.csdn.net/mengxiangxingdong/article/details/78827292
 * 
 * git教程：https://www.liaoxuefeng.com/wiki/0013739516305929606dd18361248578c67b8067c8c017b000
 */
public class gitSvn{
	/**
	 * git+TortoiseGit+码云在win7下使用
	 * 参考：http://blog.csdn.net/u012005313/article/details/54983475
	 */
	public void git(){
			//实验室里面用到的是svn，而现在使用的是git。
			先介绍一下git，tortoiseGit和码云（类似的产品还有Coding）的概念
			git是分布式版本控制系统（distributed version control system）
			//tortoiseGit是windows下git的可视化图形客户端
			码云是国内的在线代码托管平台，类比github/Coding等产品，你也可以使用自己的服务器来搭建代码托管平台。
			
			先安装git
			git官网：https://git-scm.com/ 
			在官网上下载速度太慢，我找了百度软件中心的git：http://rj.baidu.com/search/index/?kw=git
			git的安装过程只需要注意安装路径，其他的选择默认选项即可。
			目前的git安装目录：D:\Downloads\git
			
			接下来是tortoiseGit的安装
			tortoiseGit官网：https://tortoisegit.org/
			tortoiseGit安装过程也一路默认即可，只需要注意下安装路径
			目前tortoiseGit的安装目录：D:\Downloads\git\Git
			
			汉化：tortoiseGit的语言包安装目录：D:\Downloads\git\Languages
			//当你安装完成后，鼠标右键弹出菜单中会出现以下信息：
			Git GUI Here
			Git Bash Here
			Git Clone
			Git Create repository here
			TortoiesGit
			注：如果不存在可能需要重启计算机
	}

	IDEA git ：https://blog.csdn.net/autfish/article/details/52513465
		1、新建项目或者已有项目
		2、VCS/ import into version control / create git respository 在项目的路径下创建一个git仓库
		3、在码云或github上创建项目
		4、找到.git目录，强制拉取码云上的项目到本地，获取码云上的相关文件比如：README.md
		5、在IDEA里就可以参考以上的链接操作了。

	public void git的入门使用() {
		配置tortoiseGit:参考：http://blog.csdn.net/u012005313/article/details/54983475
		新建本地git服务器端仓库	
			比如：在C盘新建空文件夹 testGit，在 testGit 
			鼠标右键点击 Git Create Repository Here... git在这里创建版本库
			点击 OK按钮，等待仓库初始化完成，创建成功后，会多一个隐藏的文件.git文件夹
		commit是把文件提交到本地仓库，push是把本地仓库里的文件推送到服务器端。
		服务器端：包括git本地服务器端，还有远程的服务器端（比如：码云上的仓库）
		
		
		如果只在自己电脑上本地操作：可以新建git本地服务器仓库（.git）,在新建自己的工作区，比如work文件夹，在work文件夹里，
		可以通过git clone 把git本地服务器仓库copy到自己的work文件夹中（工作区），这样work里的文件就会Git管理了。
		一般步骤：把work里的文件，通过add命令添加到git暂存区，然后把暂存区的文件,通过commit命令提交，再把commit的文件push到本地git服务器端仓库
		省略add操作的步骤：直接把work里的文件，commit，然后执行push，把文件推送到git本地服务器端仓库。
		
		若使用码云在线服务器端仓库，可以在码云上创建git服务器端仓库，具体操作，雷同上面的本地操作.只是url变成了远程的码云上的仓库地址.
		
		个人使用：
		1、在码云上创建远程服务器仓库
		2、eclipse集成了git插件，在eclipse导入git项目，把码云上的仓库项目导入到本地
		3、写代码，team / commit 提交代码到码云服务器仓库
		4、其他同事，也是在eclipse导入git项目，把码云上的仓库项目导入到本地，然后自己建立分支，写代码，提交到码云上。这时码云上就有了2个分支，一个主分支，一个其他同事的分支。
		5、合并分支：都提交后，就可以合并对方提交的数据，先pull，从码云仓库，pull到本地服务器仓库，然后merge
		6、如果我修改了代码但是没提交，这时merge对方的代码，会出现问题，弹出框有：ok、commit、reset、等选项。OK、commit按钮并没什么用，如果选择reset是还原版本的意思。
		整个仓库版本都会还原，不是针对某个一个文件的，要慎重操作，最好是大家都提交后，再进行merge操作。
		7、merge时，git是一行一行比较的，同一行若2个人都修改了，就会冲突。<<<head是主分支的代码开始，>>>是其他分支的代码开始，中间通过====隔开对方的代码
		8、若没有冲突，在把对方的代码合并到你的分支里。
		
		使用tortoiseGit，某个文件，commit后，push后
		其他人对该文件也commit、push时，若有冲突，则push失败，这时需要pull下（pull=fetch+merge），服务器端的代码就会和
		本地的代码合并，如果有冲突，则需要你手动合并，手动合并完，tortoiseGit/resovle 告知git已经resovle解决了冲突，
		这时再commit，在push就成功了。
	}
	
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
}