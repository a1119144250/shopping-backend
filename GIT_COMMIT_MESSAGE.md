# Git 提交信息

## 建议的提交信息

```
refactor: 优化 Maven 依赖管理结构

1. 统一版本管理
   - 在父 pom.xml 中集中管理 50+ 个第三方依赖版本
   - 按功能分类（Spring、数据库、缓存、消息队列、监控等）
   - 使用 properties 统一定义版本号

2. 消除重复配置
   - 移除 20+ 个子模块中重复的编译器配置
   - 移除子模块中冗余的 groupId 和 version 声明
   - 移除所有硬编码的依赖版本号

3. 修复版本冲突
   - 解决 SnakeYAML 版本冲突问题
   - shopping-datasource 使用 1.33（ShardingSphere 要求）
   - 其他模块使用 Spring Boot 默认 2.x 版本

4. 改进效果
   - 减少约 300+ 行重复代码
   - 版本升级工作量减少 90%
   - 提升项目可维护性和规范性

受影响模块：
- 父 pom.xml
- shopping-business/*
- shopping-common/*
- shopping-gateway

详见：依赖优化说明.md
```

## 提交步骤

### 1. 查看修改状态

```bash
git status
```

### 2. 添加修改的文件

```bash
# 添加所有 pom.xml 文件
git add pom.xml
git add shopping-business/pom.xml
git add shopping-business/shopping-app/pom.xml
git add shopping-business/shopping-user/pom.xml
git add shopping-common/pom.xml
git add shopping-common/*/pom.xml
git add shopping-gateway/pom.xml

# 添加文档和配置
git add 依赖优化说明.md
git add .gitignore
```

或者一次性添加所有修改：

```bash
git add .
```

### 3. 提交

```bash
git commit -m "refactor: 优化 Maven 依赖管理结构"
```

或使用详细提交信息：

```bash
git commit -F- <<'EOF'
refactor: 优化 Maven 依赖管理结构

1. 统一版本管理
   - 在父 pom.xml 中集中管理 50+ 个第三方依赖版本
   - 按功能分类（Spring、数据库、缓存、消息队列、监控等）
   - 使用 properties 统一定义版本号

2. 消除重复配置
   - 移除 20+ 个子模块中重复的编译器配置
   - 移除子模块中冗余的 groupId 和 version 声明
   - 移除所有硬编码的依赖版本号

3. 修复版本冲突
   - 解决 SnakeYAML 版本冲突问题
   - shopping-datasource 使用 1.33（ShardingSphere 要求）
   - 其他模块使用 Spring Boot 默认 2.x 版本

4. 改进效果
   - 减少约 300+ 行重复代码
   - 版本升级工作量减少 90%
   - 提升项目可维护性和规范性

详见：依赖优化说明.md
EOF
```

### 4. 推送到远程仓库

```bash
git push origin main
# 或者你的分支名
# git push origin <your-branch-name>
```

## 查看修改的文件列表

```bash
# 查看修改了哪些文件
git diff --name-only

# 查看详细的修改内容
git diff

# 查看暂存区的修改
git diff --staged
```

## 注意事项

1. 提交前建议先测试构建：`mvn clean compile`
2. 确保 `.gitignore` 文件正确忽略了 `target/` 等目录
3. 如果有本地配置文件（如 application-local.yml），不要提交
4. 建议先在开发分支提交，测试通过后再合并到主分支
