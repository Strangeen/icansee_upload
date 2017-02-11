# 使用文档（2017-01-05）

> ## 概述

为便于JavaWeb后台接收上传文件并查看上传进度，基于cos并借鉴Jfinal接收文件的代码，对该部分代码进行修改、升级和封装，实现了上传参数统一/分离设置相结合，文件、信息接收，上传进度保存、获取等功能，并且兼顾设置简单，调用方便等优点。

> ## Quick Start （使用简例）


* ### 示例代码1：
  
  ```java
  FileUploadRequest fileRequest = new FileUploadRequest(request, sessionKey);
  UploadFile file = fileRequest.getFile("file");
  ```
  
  * ##### 描述：
  
    后台获取&lt;input type='file' name='file'&gt;的上传文件，获取方法：
    1. 创建FileUploadRequest
    2. 获取文件方法FileUploadRequest.getFile
  
  * ##### 参数说明：
  
    request - HttpServletRequest

    sessionKey - 将上传进度百分比值保存到以sessionKey为key值的session中
  
* ### 示例代码2：
  
  ```java
  Object processObj = ProcessGetter.getProcess(request, sessionKey);
  ```
  
  * ##### 描述：
  
    获取上传进度百分比值，调用默认进度获取方法ProcessGetter.getProcess，当上传完成或上传出错后会在下一次获取进度时自动删除该session。
	如果没有调用FileUploadRequest(request, sessionKey)构造器或默认的ProcessGetter.getProcess方法，用户需要自行管理进度session的创建和销毁。
  
  * ##### 参数说明：
  
    request - HttpServletRequest

    sessionKey - 将上传进度百分比值从以sessionKey为key的session中取出


> ## 文档

JDK1.7及以上

---

### 包 online.dinghuiye.icanseeupload.core

### **类 GlobalConfig**

* ### 描述： 
  
  用于上传文件参数的全局设置

* ### 构造器：
  
  > public GlobalConfig()
  
* ### 方法：
  
  > public GlobalConfig setBaseUploadPath(java.lang.String baseUploadPath)
  
  - 描述：
  
    全局设置，文件保存基础路径，默认为“upload”，即文件保存到WebRoot/upload下
  
  - 参数:

    baseUploadPath - 文件保存路径，具体设置对照如下：

    设值 | 路径
    ---|---
    null | WebRoot/upload (默认)
    path | WebRoot/path (推荐)
    " "（空格） | WebRoot
    fi le | WebRoot/fi_le
    / 或 \ | linux: /, windows: 部署网站磁盘根目录, 如: D:/
    /path | linux：/path, windows: 部署网站磁盘根目录下的文件夹, 如: D:/path
    X:\ | windows: 磁盘根目录, 如：D:/
    
  - 返回：
      
    该GlobalConfig对象

  > public GlobalConfig setMaxPostSize(java.lang.Integer maxPostSize)
  
  - 描述：
    
    全局设置，文件最大上传大小，默认为10M，如果一个表单中有多个&lt;input type='file'&gt;，那么总大小为所有input文件的总和
  
  - 参数:
  
    maxPostSize - 上传文件大小
  
  - 返回:
  
    该GlobalConfig对象
  
  > public GlobalConfig setEncoding(java.lang.String encoding)

  - 描述：
  
    全局设置，上传参数值输出的编码，默认为"utf-8"
  
  - 参数:
  
    encoding - 参数值输出编码
    
  - 返回:
  
    该GlobalConfig对象

  > public GlobalConfig setFileRenamePolicy(com.oreilly.servlet.multipart.FileRenamePolicy fileRenamePolicy)
  
  - 描述：
  
    全局设置，文件保存为的文件名，默认为上传文件文件名
  
  - 参数:
  
    fileRenamePolicy - 文件名保存规则，默认提供：
    new DefaultFileRenamePolicy() （默认）
    new UUIDFileRenamePolicy() （推荐）
  
  - 返回:
  
    该GlobalConfig对象

### **类 FileUploadRequest**

* ### 描述： 
  
  HttpServletRequest实现类，用于代替servlet中的request获取上传文件以及同时提交的参数信息。

* ### 构造器：
  
  > public FileUploadRequest(javax.servlet.http.HttpServletRequest realRequest)
  
  - 描述：
  
    构造获取文件的Request，不保存进度
  
  - 参数:
  
    realRequest - HttpServletRequest
  
  > public FileUploadRequest(javax.servlet.http.HttpServletRequest realRequest, String sessionKey) 
  
  - 描述：
  
    构造获取文件的Request，并且将进度保存到以sessionKey为key的session中（推荐）
  
  - 参数:
  
    realRequest - HttpServletRequest
    sessionKey - 保存进度到session的key值
  
  > public FileUploadRequest(javax.servlet.http.HttpServletRequest realRequest, ProcessObserver procsObsv) 
  
  - 描述：
  
    构造获取文件的Request，并且可以通过参数procsObsv自定义保存进度，推荐使用默认保存器DefProcessObserver
  
  - 参数:
  
    realRequest - HttpServletRequest
    procsObsv - 进度保存器
  
  > public FileUploadRequest(javax.servlet.http.HttpServletRequest realRequest, ProcessObservable procsObsvb) 
  
  - 描述：
  
    构造获取文件的Request，并且可以通过传入参数procsObsvb自定义监视和保存进度，procsObsvb需实现ProcessObservable接口
  
  - 参数:
  
    realRequest - HttpServletRequest
    procsObsvb - 进度监视器
  

* ### 方法：

  > public FileUploadRequest setUploadPath(java.lang.String uploadPath)

  - 描述：
  
    本次请求有效，文件保存子路径，如设置为“folder”，文件将保存到WebRoot/upload/folder下，其中，upload为全局基础路径。
	如果uploadPath为绝对路径则将本次请求的文件保存路径设置为该绝对路径
  
  - 参数:
  
    baseUploadPath - 文件保存的子路径，具体设置对照如下：
    
    设值 | 路径
    ---|---
    null | WebRoot/upload (默认)
    path | WebRoot/upload/path
    fi le | WebRoot/upload/fi_le
    " "（空格） | WebRoot
    / 或 \ | linux: /, windows: 部署网站磁盘根目录, 如: D:/
    /path | linux：/path, windows: 部署网站磁盘根目录下的文件夹, 如: D:/path
    X:\ | windows: 磁盘根目录, 如: D:/

  - 返回:
    
    该FileUploadRequest对象

  > public FileUploadRequest setMaxPostSize(int maxPostSize)

  - 描述：
  
    本次请求有效，文件最大上传大小
  
  - 参数:
    
    maxPostSize - 文件上传大小
  
  - 返回:
    
    该FileUploadRequest对象
  
  > public FileUploadRequest setEncoding(java.lang.String encoding)

  - 描述：
  
    本次请求有效，上传参数值输出的编码
  
  - 参数:
    
    encoding - 上传参数值输出的编码
  
  - 返回:
    
    该FileUploadRequest对象
  
  > public FileUploadRequest setFileRenamePolicy(com.oreilly.servlet.multipart.FileRenamePolicy fileRenamePolicy)

  - 描述：

    本次请求有效，文件保存为的文件名，默认为上传文件文件名
  
  - 参数:
  
    fileRenamePolicy - 文件名保存规则，默认提供：
    new DefaultFileRenamePolicy() （默认）
    new UUIDFileRenamePolicy() （推荐）
  
  - 返回:
  
    该FileUploadRequest对象

  > public java.util.Enumeration<java.lang.String> getParameterNames()
  
  - 描述：
  
    重写HttpServletRequest的getParameterNames方法
  
  - 返回:
    
    所有上传参数名称的Enumeration

  > public java.lang.String getParameter(java.lang.String name)
  
  - 描述：
  
    重写HttpServletRequest的getParameter方法

  - 参数：
    
    name - 参数名称
  
  - 返回:
    
    参数的值
  
  > public java.lang.String[] getParameterValues(java.lang.String name)
  
  - 描述：
  
    重写HttpServletRequest的getParameterValues方法

  - 参数：
    
    name - 参数名称
  
  - 返回:
    
    参数的所有值
  
  > public java.util.Map<java.lang.String,java.lang.String[]> getParameterMap()
  
  - 描述：
  
    重写HttpServletRequest的getParameterMap方法

  - 返回:
    
    参数名称和值得Map
  
  > public UploadFile getFile()
  
  - 描述：
  
    ==获取最后一个上传的文件（倒序获取）==

  - 返回:
  
    上传文件的UploadFile对象

  > public UploadFile getFile(java.lang.String parameterName)
  
  - 描述：
  
    获取指定参数名称的文件

  - 参数：
    
    parameterName - 文件所在&lt;input&gt;的name值
  
  - 返回:
  
    上传文件的UploadFile对象
  
  > public UploadFile getFile(java.lang.String parameterName, java.lang.String uploadPath)

  - 描述：
  
    获取指定参数名称的文件，并设置上传子路径

  - 参数：
    
    parameterName - 文件所在&lt;input&gt;的name值
    uploadPath - 子路径，同FileUploadRequest参数设置
  
  - 返回:
  
    上传文件的UploadFile对象	
  
  > public UploadFile getFile(java.lang.String parameterName, java.lang.String uploadPath, int maxPostSize)
  
  - 描述：
  
    获取指定参数名称的文件，并设置上传子路径和最大文件大小

  - 参数：
    
    parameterName - 文件所在&lt;input&gt;的name值
    uploadPath - 子路径，同FileUploadRequest参数设置
    maxPostSize - 最大文件大小，同FileUploadRequest参数设置
  
  - 返回:
  
    上传文件的UploadFile对象
  													
  > public UploadFile getFile(java.lang.String parameterName, java.lang.String uploadPath, java.lang.Integer maxPostSize, com.oreilly.servlet.multipart.FileRenamePolicy fileRenamePolicy)
  		
  - 描述：
  
    获取指定参数名称的文件，并设置上传文件保存子路径、最大文件大小和文件名保存规则

  - 参数：
    
    parameterName - 文件所在&lt;input&gt;的name值
    uploadPath - 子路径，同FileUploadRequest参数设置
    maxPostSize - 最大文件大小，同FileUploadRequest参数设置
    fileRenamePolicy - 文件名保存规则，同FileUploadRequest参数设置
  
  - 返回:
  
    上传文件的UploadFile对象
  											
  > public UploadFile getFile(java.lang.String parameterName, java.lang.String uploadPath, java.lang.Integer maxPostSize, com.oreilly.servlet.multipart.FileRenamePolicy fileRenamePolicy, java.lang.String encoding)
  	
  - 描述：
  
    获取指定参数名称的文件，并设置上传文件保存子路径、最大文件大小、上传参数值输出编码和文件名保存规则

  - 参数：
    
    parameterName - 文件所在&lt;input&gt;的name值
    uploadPath - 子路径，同FileUploadRequest参数设置
    maxPostSize - 最大文件大小，同FileUploadRequest参数设置
    fileRenamePolicy - 文件名保存规则，同FileUploadRequest参数设置
	encoding - 上传参数值输出编码，同FileUploadRequest参数设置
  
  - 返回:
  
    上传文件的UploadFile对象
  
  > public java.util.List<UploadFile> getFiles()
  
  - 描述：
  
    获取所有上传文件

  - 返回:
  
    上传文件的UploadFile对象List集合
  
  > public java.util.List<UploadFile> getFiles(java.lang.String uploadPath)
  
  - 描述：
  
    获取所有上传文件，并设置上传文件保存子路径

  - 参数：
    
    uploadPath - 子路径，同FileUploadRequest参数设置
  
  - 返回:
  
    上传文件的UploadFile对象List集合
  
  > public java.util.List<UploadFile> getFiles(java.lang.String uploadPath, int maxPostSize)
  		
  - 描述：
  
    获取所有上传文件，并设置上传文件保存子路径和最大文件大小

  - 参数：
    
    uploadPath - 子路径，同FileUploadRequest参数设置
    maxPostSize - 最大文件大小，同FileUploadRequest参数设置
  
  - 返回:
  
    上传文件的UploadFile对象List集合
  																			 
  > public java.util.List<UploadFile> getFiles(java.lang.String uploadPath, java.lang.Integer maxPostSize, com.oreilly.servlet.multipart.FileRenamePolicy fileRenamePolicy)
  	
  - 描述：
  
    获取所有上传文件，并设置上传文件保存子路径、最大文件大小和文件名保存规则

  - 参数：
    
    parameterName - 文件所在&lt;input&gt;的name值
    uploadPath - 子路径，同FileUploadRequest参数设置
    maxPostSize - 最大文件大小，同FileUploadRequest参数设置
    fileRenamePolicy - 文件名保存规则，同FileUploadRequest参数设置
  
  - 返回:
  
    上传文件的UploadFile对象List集合
  																				 
  > public java.util.List<UploadFile> getFiles(java.lang.String uploadPath, java.lang.Integer maxPostSize, com.oreilly.servlet.multipart.FileRenamePolicy fileRenamePolicy, java.lang.String encoding)
  
  - 描述：
  
    获取所有上传文件，并设置上传文件保存子路径、最大文件大小、上传参数值输出编码和文件名保存规则

  - 参数：
    
    parameterName - 文件所在&lt;input&gt;的name值
    uploadPath - 子路径，同FileUploadRequest参数设置
    maxPostSize - 最大文件大小，同FileUploadRequest参数设置
    fileRenamePolicy - 文件名保存规则，同FileUploadRequest参数设置
	encoding - 上传参数值输出编码，同FileUploadRequest参数设置
  
  - 返回:
  
    上传文件的UploadFile对象List集合
  
  > **注意：UploadRequest和getFile中的上传设置只有一次会生效，为第一次接受文件或参数时的设置**

  

### 包 online.dinghuiye.icanseeupload.core.consts

### **类 Consts**
  
* ### 属性：
  
  > public static java.lang.String DEF_BASE_UPLOAD_PATH
  
  - 描述：
  
    默认文件上传保存的基础路径，值 upload
  
  > public static int DEF_MAX_POST_SIZE

  - 描述：
  
    默认文件上传最大大小，值 10M
  
  > public static java.lang.String DEF_ENCODING

  - 描述：
  
    默认参数值输出编码，值 UTF-8

  > public static com.oreilly.servlet.multipart.FileRenamePolicy DEF_FILE_REN_POLICY
  
  - 描述：
  
    默认文件名保存规则，值 new com.oreilly.servlet.multipart.DefaultFileRenamePolicy()

  > public static final int K
  
  - 描述：

    常量字段，K单位，值 1024

  > public static final int M

  - 描述：
  
    常量字段，M单位，值 1048576

  > public static final int G

  - 描述：
  
    常量字段，G单位，值 1073741824


### 包 online.dinghuiye.icanseeupload.core.kit

### **类 PathKit**

* ### 方法：

  > public static java.lang.String getWebRootPath()
  
  - 描述：
  
    默认文件上传保存的基础路径，值 upload
  
  - 返回：
    
    网站部署的目录路径，即WebRoot绝对路径，末尾没有 /

  > public static boolean isAbsolutelyPath(java.lang.String path)

  - 描述：
  
    判断路径是否为绝对路径，如：window以 X:/ 开头，linux以 / 开头

  - 参数：
  
    需要判断的路径
  
  - 返回：
    
    是 true, 否 false

---

> ## 缺点

* 不支持&lt;input type="file"&gt;的multiple="multiple"参数，如果设置multiple上传，只能获取到最后一个文件的信息

* 上传文件太大，后台会报错“IOException：Posted content length of ... exceeds limit of ...”，并停止文件接收，==response不回应==，导致前端报错：“POST ... net::ERR_CONNECTION_ABORTED”，此时如果使用ajax上传问会执行error函数

  Jfinal的做法应该是报错后继续接收文件流直到完成，再回应前端（待验证），如果确实如此，我认为这样的做法并不是很好，如果上传文件大小超出文件最大限制，需要等待较长时间才能收到后台反馈，所以我修改为直接报错。另外，建议前端控件限制上传文件大小，可以避免后台报错，==当然，后期的TODO就是找到解决办法==
