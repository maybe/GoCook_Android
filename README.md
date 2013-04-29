GoCook
======

GoCook Android Client

/*******  Progress *******/
在BaseFragment中增加了Progress loading View。
使用时只需调用showProgress(boolean)函数，参数为true表示显示，false隐藏。
还可以设置loading时的显示文字，默认显示“加载中...”，修改需调用setProgressMessage()方法。
