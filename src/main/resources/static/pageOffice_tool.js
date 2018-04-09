function getOpenClick(filePath){
    //此处的filePath是从服务器传递过来的服务器文件的绝对路径，客户端是访问不了的。。。
    //因为filePath可能会包含中文和特殊字符所以需要进行转码
    var height=screen.availHeight;
    var width=screen.availWidth;
    return "<a href=\"javascript:POBrowser.openWindowModeless('/openPageOffice?filePath="+encodeURIComponent(encodeURIComponent(filePath))+"','width="+width+";height="+height+";');\">"+filePath+" </a>";
}