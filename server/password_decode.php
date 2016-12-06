<?php 

function password_decode($input_password){
	//Key for decoding $input_password
	$key = ('CyConnect');
	//String for storind decoded password
	$output_password = '';
	//This decodes the input_password by XORing each character in input_password with a character in $key, which repeats until input_password is decoded
	for($i=0; $i<strlen($input_password);){
		for($j=0; $j<strlen($key) && $i<strlen($input_password); $i++, $j++){
			$output_password .= $input_password{$i} ^ $key{$j};
		}
	}
	//return the decoded password
	return $output_password;
}

?>
