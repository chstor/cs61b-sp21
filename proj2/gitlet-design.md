# Gitlet Design Document
.git的目录结构
- HEAD	指向当前分支
- config	仓库配置信息
- description	仅 GitWeb 使用
- hooks/	Git 钩子（自动化脚本）
- index	暂存区（staging area）

- info/	额外信息（如 .gitignore）

- logs/	记录所有分支的历史，两个文件夹：refs和HEAD文件

.git/logs/refs/heads：文件夹，默认有master文件

.git/logs/refs/remotes：文件夹，存储的是远程的所有分支对象

.git/logs/HEAD：文件，保存的是所有操作记录

- objects/	Git 的核心存储（commit、blob、tree）

## **blob（文件对象）**

**blob（Binary Large Object）是 Git 用来存储文件内容的对象。**

- **每个文件的内容都会被单独存储为一个 blob**。
- **blob 只存储文件的内容，不存储文件名**。

## **tree（树对象）**

**tree 记录目录结构，它存储的是文件名和它们的 blob 哈希值，以及子目录的 tree 哈希值。**

- **每个目录（包括根目录）都对应一个 tree 对象**。
- **tree 记录该目录下的所有文件（文件名 + 对应 blob）和子目录（子目录名 + 对应 tree）**。

## **commit（提交对象）**

**commit 代表一次提交，记录了当前 tree（根目录）、父 commit、提交信息、时间戳等信息。**



这三个对象通过 SHA-1 哈希值相互连接，共同构成 Gitlet 或 Git 的版本管理系统。

每个提交（`Commit`）指向一个目录树（`Tree`），而每个目录（`Tree`）又包含多个文件（`Blob`）。

通过这种方式，Git 实现了高效的文件版本控制和历史记录追踪。

**Blob** 对象：存储文件的内容，每个文件内容有一个唯一的 SHA-1 哈希值。

**Tree** 对象：存储目录结构，包括文件和子目录的哈希值。

**Commit** 对象：存储一次提交的元数据，指向当前版本的 `Tree` 对象并记录父提交。

- refs/heads	记录分支和标签信息

**Name**:

## Classes and Data Structures

### Class 1

#### Fields

1. Field 1
2. Field 2


### Class 2

#### Fields

1. Field 1
2. Field 2


## Algorithms

## Persistence

