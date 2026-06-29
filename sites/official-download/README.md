# 画质怪兽2026 · 超轻量官网

## 拆分部署（推荐）

| 资源 | 服务器 | 地址 |
|------|--------|------|
| **官网 HTML** | 海外 `43.134.129.133` | `https://画质怪兽.cn/` |
| **APK 安装包** | 国内 `101.43.236.54` | `https://monster.hk.cn/download/apk/delta-helper-2026.apk` |

官网 **HTML + 4 张压缩图标（约 11KB）**，海外秒开；点击下载走 **国内带宽**，不慢。

## 1. 海外：上传官网

```bash
scp sites/official-download/index.html \
  root@43.134.129.133:/www/wwwroot/画质怪兽.cn/index.html

scp -r sites/official-download/icons \
  root@43.134.129.133:/www/wwwroot/画质怪兽.cn/
```

Nginx 海外站 **只需**：

```nginx
root /www/wwwroot/画质怪兽.cn;
index index.html;

gzip on;
gzip_types text/html;

location = / {
    try_files /index.html =404;
    add_header Cache-Control "no-cache";
}

location / {
    return 404;
}
```

不要放 UniApp H5、不要 `/api/` 反代、不要 `apk/` 目录。

## 2. 国内：上传 APK

```bash
ssh root@101.43.236.54 "mkdir -p /www/wwwroot/monster.hk.cn/download/apk"

scp apps/delta-helper-app/release/online/release/delta-helper-app-online-release.apk \
  root@101.43.236.54:/www/wwwroot/monster.hk.cn/download/apk/delta-helper-2026.apk
```

Nginx 国内 `monster.hk.cn` 增加：

```nginx
location ~* ^/download/apk/.+\.apk$ {
    root /www/wwwroot/monster.hk.cn;
    add_header Content-Type application/vnd.android.package-archive;
    add_header Content-Disposition 'attachment; filename="delta-helper-2026.apk"';
}
```

## 3. 验收

```bash
# 海外官网应 <1s
curl -s -o /dev/null -w "官网 total:%{time_total}s size:%{size_download}\n" https://画质怪兽.cn/

# 国内 APK 应快
curl -sI https://monster.hk.cn/download/apk/delta-helper-2026.apk | head -5
```

## 发版

- 新 APK → 只更新国内 `apk/delta-helper-2026.apk`
- 版本号变了 → 改 `index.html` 里 `v1.0.3` 一行，重新 scp 到海外

## 推广

**画质怪兽.cn** — 短、好记；打开秒开，点下载走国内。
