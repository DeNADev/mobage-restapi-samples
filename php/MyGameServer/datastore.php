<?php
/**
 * The MIT License (MIT)
 * Copyright (c) 2014 DeNA Co., Ltd.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

$filename=".cache";

function write_array_to_filecache($array_to_write) {
	global $filename;
	
	$string = "";
	foreach ($array_to_write as $key => $val) {
		$string .= "$key::=$val\n";
	}
	file_put_contents($filename, $string);
}

function read_array_from_filecache() {
	global $filename;
	
	$lines = file($filename);
	$arr = array();
	
	foreach ($lines as $line) {
		list($key, $val) = explode("::=", $line);
		$arr[trim($key)] = trim($val);
	}
	
	return $arr;
}

function store_array_to_session($array_to_write) {
	if (!isset($_SESSION)) {
	 	session_start();
	}

	if (isset($_SESSION)) {
		foreach ($array_to_write as $key => $val) {
			$_SESSION[$key] = $val;
		}
	}
}

function read_array_from_session() {
	if (!isset($_SESSION)) {
	 	session_start();
	}

	if (isset($_SESSION)) {
		return $_SESSION;
	}

	return null;
}

?>