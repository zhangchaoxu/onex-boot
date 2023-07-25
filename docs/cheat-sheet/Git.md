# Git

### 删除隐私数据
Github等公开库中误操作提交了隐私数据，比如服务器IP、账号、密码、各种开放平台的私钥等。如何删除。        
先说结论，放弃抵抗，尝试更换密码、重置私钥，任何信息在互联网公开后无法挽回。      
网上一般提到的[bfg-repo-cleaner](https://rtyley.github.io/bfg-repo-cleaner/)和git filter-repo都只能帮助删除git的提交记录，若知道commit id，照样可以访问到隐私数据文件。        
对于commit id的记录，只能通过github的官方支持提交工单做支持。  
若repo已经被人fork，就更没救了。

ref [Removing sensitive data from a repository](https://docs.github.com/en/authentication/keeping-your-account-and-data-secure/removing-sensitive-data-from-a-repository)
