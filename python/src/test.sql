CREATE TABLE `user` (
    `id` int(11) NOT NULL AUTO_INCREMENT,
    `email` varchar(255) COLLATE utf8_bin  NULL,
    `password` varchar(255) COLLATE utf8_bin NOT NULL,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin
    AUTO_INCREMENT=1 ;
ALTER TABLE `user`
  ADD COLUMN `name`  varchar(255) NULL AFTER `password`;



CREATE TABLE `message` (
    `id` int(11) NOT NULL AUTO_INCREMENT,
    `message` varchar(255) DEFAULT NULL,
    `name` varchar(255) DEFAULT NULL,
    `to` varchar(255) DEFAULT NULL,
    `time` datetime DEFAULT NULL,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

