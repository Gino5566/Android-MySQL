<?php
function delFileUnderDir( $dirName="D:/xampp/htdocs/Upload_Image_Android/images" )
{
    if ( $handle = opendir( "$dirName" ) ) {

        while ( false !== ( $item = readdir( $handle ) ) ) {
            if ( $item != "." && $item != ".." ) {
                if ( is_dir( "$dirName/$item" ) ) {
                      delFileUnderDir( "$dirName/$item" );
                } else {
                    if( unlink( "$dirName/$item" ) );
                }
                }
        }
        closedir( $handle );
    }
}
delFileUnderDir();
$con= mysqli_connect("localhost","帳號","密碼","upload_image");
if($con){
    if(!empty($_POST['image'])){
        $path='images/'.date("d-m-Y").'-'.time().'-'.rand(10000,100000).'.jpg';
        if(file_put_contents($path,
        base64_decode($_POST['image']))){
            $sql = "TRUNCATE TABLE images";
            $con->query($sql) === TRUE;

            $sql="insert into images (path) values ('".$path."')";

            if(mysqli_query($con,$sql)){
                $response = file_get_contents('http://localhost/ListView_Android/go.php');
                echo $response;
            }
            else echo"導入資料庫失敗";
        }
        else echo "上傳圖片失敗";
    }
    else echo"沒有圖片";
}
else echo"資料庫連線失敗";
?>  