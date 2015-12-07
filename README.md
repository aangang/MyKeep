# MyKeep
## 项目说明
 符合MD设计的记事本应用
## 说明
 - 设计上符合MD设计风格
 - 使用ContentProvider方式来管理数据库
 - 使用观察者模式来接偶各个模块间数据的传递和通知
 - 使用SVG图片来作为资源图，占用大小更小，适配更简单
 - 使用fragment作为记事详情页面，打开速度更优，测试渲染打开速度大概10ms-20ms以内，比使用activity快大概200%
 - 使用Toolbar RecyclerView等控件来优化UI的显示效果
## 已知问题
 稍后编辑
## TODO List
  - 主页到详情页面（fragment）的跳转效果
  - Floating Button bug修复
  - 记事本的长按删除功能
  - 增加记事页面闹钟提醒功能
  - 增加记事页面图片添加功能
  - 增加记事本页面调色板功能，清单记事优先级
  - 增加记事搜索功能
  - 回收站功能，可以还原删除的某些记录（比如优先最新的若干条）
  - 同步功能，可以同步到云盘备份和还原备份的功能
  - 增加语音输入和语音保存功能
  - 增加截取网络页面功能，可以直接保存到记事本中，类似于pocket
  - 增加常用平台的分享转发功能
  - 可能增加帐号体系，可以直接同步数据~