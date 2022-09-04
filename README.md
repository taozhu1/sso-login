# 目录结构

```bash
├─common	# 公共组件
│  └─target
│      ├─classes
│      │  └─com
│      │      └─tao
│      │          └─common
│      │              ├─config	# 配置文件
│      │              ├─domain	# 模型
│      │              ├─uitl	# 工具类
│      │              └─vo		# 视图模型
├─sso-center	# sso 服务
│  ├─src
│  │  ├─main
│  │  │  ├─java
│  │  │  │  └─com
│  │  │  │      └─tao
│  │  │  │          └─ssocenter
│  │  │  │              └─controller	# api层
│  │  │  └─resources
│  │  │      ├─static
│  │  │      └─templates
└─sso-client	# user client
    ├─src
    │  ├─main
    │  │  ├─java
    │  │  │  └─com
    │  │  │      └─tao
    │  │  │          └─ssoclient
    │  │  │              ├─config		# mvc配置
    │  │  │              ├─controller	# api层
    │  │  │              └─filter		# 拦截器
    │  │  └─resources
    │  │      ├─static
    │  │      └─templates
```

