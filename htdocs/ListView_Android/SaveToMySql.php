<?php 
    $folderPath='classItems';
    $con = mysqli_connect("localhost", "帳號", "密碼", "listviewitem");
    if($con){
        $files=scandir($folderPath);

        $sql = "TRUNCATE TABLE classitems";
            if ($con->query($sql) === TRUE){

                foreach ($files as $file) {
                    // 排除當前路徑（.）和上層路徑（..）
                    if ($file != "." && $file != "..") {

                        $filePath = $folderPath . '/' . $file;
                

                        $sql = "INSERT INTO classitems (name, path) VALUES ('$file', '$filePath')";
                
                        if ($con->query($sql) === TRUE) {
                            
                        } else {
                            echo "上傳資料庫錯誤: " . $con->error;
                        }
                    }
                }

                $result = $con -> query("SELECT * FROM `classitems`");
                while ($row = $result->fetch_assoc()) //當該指令執行後有回傳
                {
                    $output[] = $row; // 就逐項將回傳的東西放到陣列中
                }
                if(!empty($output)){
                    print(json_encode($output, JSON_UNESCAPED_UNICODE));
                }
                else{
                    echo"資料庫沒有資料";
                }
                $con -> close(); 
            }
            else echo"清理空間失敗";
    }
    else echo"資料庫連線失敗";

?>