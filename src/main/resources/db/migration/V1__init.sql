CREATE TABLE IF NOT EXISTS `koin_payment`.`order`
(
    `id`                    INT UNSIGNED    NOT NULL AUTO_INCREMENT COMMENT '주문 ID',
    `pg_order_id`           VARCHAR(64)     NOT NULL COMMENT 'pg 주문 ID',
    `order_type`            VARCHAR(10)     NOT NULL COMMENT '주문 타입',
    `phone_number`          VARCHAR(20)     NOT NULL COMMENT '주문자 전화번호',
    `total_product_price`   INT UNSIGNED    NOT NULL COMMENT '상품 총 금액',
    `total_price`           INT UNSIGNED    NOT NULL COMMENT '주문 총 금액',
    `is_deleted`            TINYINT(1)      NOT NULL DEFAULT FALSE COMMENT '삭제 여부',
    `orderable_shop_id`     INT UNSIGNED    NOT NULL COMMENT '주문한 상점 ID',
    `user_id`               INT UNSIGNED    NOT NULL COMMENT '주문자 사용자 ID',
    `created_at`            TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성 일시',
    `updated_at`            TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정 일시',
    PRIMARY KEY (`id`)
);

CREATE INDEX idx_user_id ON `koin_payment`.`order` (user_id);
CREATE INDEX idx_orderable_shop_id ON `koin_payment`.`order` (orderable_shop_id);

CREATE TABLE IF NOT EXISTS `koin_payment`.`order_delivery`
(
    `order_id`          INT UNSIGNED    NOT NULL COMMENT '주문 ID',
    `address`           VARCHAR(100)    NOT NULL COMMENT '배달 주소',
    `to_owner`          VARCHAR(50)     NOT NULL COMMENT '사장님 전달 메시지',
    `to_rider`          VARCHAR(50)     NOT NULL COMMENT '라이더 전달 메시지',
    `delivery_tip`      INT UNSIGNED    NOT NULL COMMENT '배달비',
    `provide_cutlery`   TINYINT(1)      NOT NULL DEFAULT 0 COMMENT '수저, 포크 수령 여부',
    PRIMARY KEY (order_id),
    CONSTRAINT `fk_order_delivery_order` FOREIGN KEY (`order_id`) REFERENCES `koin_payment`.`order` (`id`)
);

CREATE TABLE IF NOT EXISTS `koin_payment`.`order_takeout`
(
    `order_id`          INT UNSIGNED    NOT NULL COMMENT '주문 ID',
    `to_owner`          VARCHAR(50)     NOT NULL COMMENT '사장님 전달 메시지',
    `provide_cutlery`   TINYINT(1)      NOT NULL DEFAULT 0 COMMENT '수저, 포크 수령 여부',
    PRIMARY KEY (order_id),
    CONSTRAINT `fk_order_pack_order` FOREIGN KEY (`order_id`) REFERENCES `koin_payment`.`order` (`id`)
);

CREATE TABLE IF NOT EXISTS `koin_payment`.`payment`
(
    `id`                INT UNSIGNED    NOT NULL AUTO_INCREMENT COMMENT '고유 ID',
    `payment_key`       VARCHAR(200)    NOT NULL COMMENT '결제 키',
    `amount`            INT UNSIGNED    NOT NULL COMMENT '결제 금액',
    `status`            VARCHAR(30)     NOT NULL COMMENT '결제 상태',
    `method`            VARCHAR(30)     NOT NULL COMMENT '결제 수단',
    `requested_at`      TIMESTAMP       NOT NULL COMMENT '결제 요청 일시',
    `approved_at`       TIMESTAMP       NOT NULL COMMENT '결제 승인 일시',
    `order_id`          INT UNSIGNED    NOT NULL COMMENT '주문 번호',
    PRIMARY KEY (`id`),
    UNIQUE KEY uq_payment_key (`id`),
    CONSTRAINT fk_payment_order FOREIGN KEY (`order_id`) REFERENCES `koin_payment`.`order` (`id`)
);

CREATE TABLE IF NOT EXISTS `koin_payment`.`payment_cancel`
(
    `id`                INT UNSIGNED    NOT NULL AUTO_INCREMENT COMMENT '결제 취소 ID',
    `transaction_key`   VARCHAR(64)     NOT NULL COMMENT '취소 트랜잭션 키',
    `cancel_reason`     VARCHAR(200)    NOT NULL COMMENT '취소 사유',
    `cancel_amount`     INT UNSIGNED    NOT NULL COMMENT '취소 금액',
    `canceled_at`       TIMESTAMP       NOT NULL COMMENT '취소 일시',
    `payment_id`        INT UNSIGNED    NOT NULL COMMENT '결제 ID',
    PRIMARY KEY (`id`),
    CONSTRAINT `fk_payment_cancel_payment` FOREIGN KEY (`payment_id`) REFERENCES `koin_payment`.`payment` (`id`)
);

CREATE TABLE IF NOT EXISTS `koin_payment`.`order_menu`
(
    `id`                INT UNSIGNED    NOT NULL AUTO_INCREMENT COMMENT '주문 메뉴 ID',
    `menu_name`         VARCHAR(255)    NOT NULL COMMENT '메뉴 이름',
    `menu_price`        INT UNSIGNED    NOT NULL COMMENT '메뉴 금액',
    `menu_price_name`   VARCHAR(255)    NULL     COMMENT '메뉴 가격 이름',
    `quantity`          INT UNSIGNED    NOT NULL COMMENT '수량',
    `order_id`          INT UNSIGNED    NOT NULL COMMENT '주문 ID',
    PRIMARY KEY (`id`),
    CONSTRAINT `fk_order_menu_order` FOREIGN KEY (`order_id`) REFERENCES `koin_payment`.`order` (`id`)
);

CREATE TABLE IF NOT EXISTS `koin_payment`.`order_menu_option`
(
    `id`                INT UNSIGNED    NOT NULL AUTO_INCREMENT COMMENT '메뉴 옵션 ID',
    `option_group_name` VARCHAR(255)    NOT NULL COMMENT '주문 메뉴 옵션 이름',
    `option_name`       VARCHAR(255)    NOT NULL COMMENT '옵션 이름',
    `option_price`      INT UNSIGNED    NOT NULL COMMENT '옵션 가격',
    `quantity`          INT UNSIGNED    NOT NULL COMMENT '옵션 수량',
    `order_menu_id`     INT UNSIGNED    NOT NULL COMMENT '주문 메뉴 ID',
    PRIMARY KEY (`id`),
    CONSTRAINT `fk_order_menu_option_menu` FOREIGN KEY (`order_menu_id`) REFERENCES `koin_payment`.`order_menu` (`id`)
);

CREATE TABLE IF NOT EXISTS `koin_payment`.`payment_idempotency_key`
(
    `id`                    INT UNSIGNED        NOT NULL AUTO_INCREMENT COMMENT '결제 멱등키 ID',
    `user_id`               INT UNSIGNED        NOT NULL COMMENT '유저 ID',
    `idempotency_key`       VARCHAR(300)        NOT NULL COMMENT '결제 멱등키',
    `created_at`            TIMESTAMP           NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성 일시',
    `updated_at`            TIMESTAMP           NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정 일시',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_idempotency_key_user_id` (`user_id`)
);

CREATE INDEX idx_user_id ON `koin_payment`.`payment_idempotency_key` (user_id);
