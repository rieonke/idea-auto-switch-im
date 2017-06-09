# idea-auto-switch-im
An IntelliJ IDEA Plugin for auto-switching Input Method in vim mode FOR MAC ONLY
一个在MAC IdeaVim下自动切换输入法的IntelliJ IDEA 插件

# Introduction
It will help to switch Input method automatically while in using vim mode in Mac Idea.

# Features

1) Auto switch to English IME while in normal/visual mode
2) Auto switch to the last IME while returning back insert mode
3) Auto switch to the last IME while idea re-focused
---
1) 在normal/visual 模式自动切换到英文输入法
2) 当从正常模式切换到插入模式自动切换上一个输入法
3) 当idea重新获得焦点时切换到上一个输入法

# Build

1) Clone this repo
2) Update submodule
```bash
git submodule update --init --recursive

```
3) Build JNI Library with Xcode
```bash
cd AutoSwitchInputSource

xcodebuild -scheme AutoSwitchInputSource DSTROOT="/path/to/your/project/folder" archive

```
then you will see a new directory named `usr` in your folder specified in `DSTROOT`,just copy the `libAutoSwitchInputSource.jnilib` to the main java project folder `resources/native` 

4) Build plugin
```bash
gradle buildPlugin
```



# License
GPL v3
