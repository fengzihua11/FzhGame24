
* 签名文件fzh20171107.keystore
* 签名信息，Alias是android.keystore，二个密码都是19841111
* MD5值：37:87:FF:37:54:2E:57:0B:15:EB:80:9C:44:C5:1B:3E

### 查看jks签名
keytool -list -v -keystore fzh20171107.keystore

### 查看apk签名
keytool -printcert -file META-INF/CERT.RSA