<?php
$output = shell_exec('py D:\xampp\htdocs\Upload_Image_Android\identify.py');
#conda activate torch && python detect.py
echo $output;
?>