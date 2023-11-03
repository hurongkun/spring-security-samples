
CREATE TABLE `hr` (
                      `id` bigint(20) NOT NULL AUTO_INCREMENT,
                      `name` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
                      `phone` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
                      `telephone` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
                      `address` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
                      `enabled` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
                      `username` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
                      `password` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
                      `userface` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
                      `remark` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
                      PRIMARY KEY (`id`),
                      UNIQUE KEY `unique_index` (`name`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE TABLE `role` (
                        `id` bigint(20) NOT NULL AUTO_INCREMENT,
                        `name` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
                        `nameZh` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
                        PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

INSERT INTO `spring_security`.`hr`(`id`, `name`, `phone`, `telephone`, `address`, `enabled`, `username`, `password`, `userface`, `remark`) VALUES (1, '张三', '15797878960', '15797878960', '中国', '1', 'zhangsan', '$2a$10$3NSLt9AhqQIRgCNEIoV7au93EK.ZLkINGQGsOJ.PZEdPzpBTcClkK', '1231312', '备注');

INSERT INTO `spring_security`.`role`(`id`, `name`, `nameZh`) VALUES (1, 'Role_admin', '张三');

