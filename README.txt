This tool use tessact orc engine and develop in eclipse IDE.
It has simple awt gui but it's very useful.
The result is store in OCR-text.txt file after program is run.

User must have these jar library in Build Path:
	ghost4j-0.3.1.jar
	jai_imageio.jar
	jna.jar
	junit-4.10.jar

Binary file is available in zip file. Extract and run !
The exe file is packed by Jsmooth

Enjoy ~~

-----------------------------------------------------------------------
中文名：截图抓字

文件结构：
temp.png 是截图
ocr-text.txt 是文字结果@___@
tesseract 包括依赖库，一个引擎

弊端：
1. 只支持英文，图转字，中文不行
2.字体太小的不认，需要放大截图  "放大的识别率给力啊"

用处：
可以看pdf的时候，不用打字去查词典。可以写个程序调用“ocr-text.txt" 快速查词汇
下阶段自己做生词本，采集这块搞定了

新浪微博: www.weibo.com/dailyjava