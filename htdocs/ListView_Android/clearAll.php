<?php
function delFileUnderDir( $dirName="D:/xampp/htdocs/ListView_Android/classItems" )
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

$con = mysqli_connect("localhost", "帳號", "密碼", "listviewitem");
if($con){
    $sql = "TRUNCATE TABLE classitems";
    $con->query($sql);
    echo "資料清理成功";
}
else echo"連結失敗"
?>