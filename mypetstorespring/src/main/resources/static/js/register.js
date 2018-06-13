var xmlHttpRequestUsername;

// 创建 xmlHttpRequestUsername
function createXMLHttpRequestUsername() {

    if(window.xmlHttpRequestUsername){//非IE浏览器

        xmlHttpRequestUsername = new XMLHttpRequest();
    }else if(window.ActiveXObject){//IE6以上版本的IE

        xmlHttpRequestUsername = new ActiveXObject("Msxm12.XMLHTTP");
    }else{//IE6及以下版本的IE
        xmlHttpRequestUsername = new ActiveXObject("Microsoft.XMLHTTP");
    }
}

// 发送请求
function sendRequestUsername(url) {
    createXMLHttpRequestUsername();
    xmlHttpRequestUsername.open("GET",url,true);
    xmlHttpRequestUsername.onreadystatechange = processResponseUsername;
    xmlHttpRequestUsername.send(null);
}

// 判断用户名是否已存在
function usernameIsExist() {

    var username = document.registerForm.username.value;

    sendRequestUsername("/account/usernameIsExist?username=" + username);
}

// 取得回复
function processResponseUsername() {

    if(xmlHttpRequestUsername.readyState == 4){
        if(xmlHttpRequestUsername.status == 200){

            // 获取 xml 文件中的msg 标签
            var responseInfo = xmlHttpRequestUsername.responseXML.
            getElementsByTagName("msg")[0].firstChild.data;

            var div1 = document.getElementById("usernameMsg");

            if(responseInfo =="Exist"){

                div1.innerHTML="<font color='red'>用户名已存在</font>";
            }else{

                div1.innerHTML="<font color='green'>用户名可用</font>";
            }
        }
    }
}
