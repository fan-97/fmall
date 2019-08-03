<html>
<body>
<h2>hahahhaha</h2>

<%--图片上传--%>
<form name="form1" action="manage/product/upload.do" method="post"  enctype="multipart/form-data">
    <input type="file" name="upload_file">
    <input type="submit" value="upload"/>
</form>

<%--富文本上传图片--%>
<form name="form2" action="manage/product/richtext_img_upload.do" method="post" enctype="multipart/form-data">
    <input type="file" name="upload_file">
    <input type="submit" value="rich_upload"/>
</form>
</body>
</html>
