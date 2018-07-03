public class 乱码{
	public void 保存到数据库为乱码(){
		优先考虑，数据库的自身的编码问题，比如MySQL，在my.ini文件里配置。
	}
	public void idea资源文件乱码(){
		在资源文件中添加如下：
		banner.charset=UTF-8
		server.tomcat.uri-encoding=UTF-8
		spring.http.encoding.charset=UTF-8
		spring.http.encoding.enabled=true
		spring.http.encoding.force=true
		spring.messages.encoding=UTF-8
	}
}