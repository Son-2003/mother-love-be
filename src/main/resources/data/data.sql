INSERT INTO `categories` (`category_name`)
VALUES
('Sữa bột pha sẵn'),
('Sữa tươi sữa chua'),
('Sữa hạt dinh dưỡng'),
('Thức uống dinh dưỡng');

INSERT INTO `brands` (`brand_name`, `image`)
VALUES
('Abbott-grow', 'https://res.cloudinary.com/dpysbryyk/image/upload/v1716958852/Milk/ABBOTT-GROW/df5ti0bn5kc8ah17m3lp.png'),
('Bubs', 'https://res.cloudinary.com/dpysbryyk/image/upload/v1716958836/Milk/Bubs/bqe4ryf5idpkgwxtzg2t.png'),
('Friso-gold', 'https://res.cloudinary.com/dpysbryyk/image/upload/v1716958839/Milk/FRISO-GOLD/nix3ctumfvkdmojab26x.png'),
('Meiji', 'https://res.cloudinary.com/dpysbryyk/image/upload/v1716958839/Milk/Meiji/o7vje2iatspfsetnpeqw.png'),
('Morinaga', 'https://res.cloudinary.com/dpysbryyk/image/upload/v1716958848/Milk/morinaga/hna55mr6aubolprapbq9.png');

INSERT INTO `products` (`description`, `image`, `price`, `product_name`, `status`, `brand_id`, `category_id`)
VALUES
('Description 1', '[https://res.cloudinary.com/dpysbryyk/image/upload/v1716958851/Milk/ABBOTT-GROW/Abbott-grow-1/bzrkshtku5u9uuuguzzn.jpg,https://res.cloudinary.com/dpysbryyk/image/upload/v1716958851/Milk/ABBOTT-GROW/Abbott-grow-1/yerou0jissrypqcfvbiv.jpg,https://res.cloudinary.com/dpysbryyk/image/upload/v1716958851/Milk/ABBOTT-GROW/Abbott-grow-1/itkarknieq14kndaiadk.jpg]', 100.00, 'Abbott-grow-1', 1, 1, 1),
('Description 2', '[https://res.cloudinary.com/dpysbryyk/image/upload/v1716958851/Milk/ABBOTT-GROW/Abbott-grow-2/zqf0qohiyjxiqa4pn8g6.jpg,https://res.cloudinary.com/dpysbryyk/image/upload/v1716958851/Milk/ABBOTT-GROW/Abbott-grow-2/mhxsyqcmiygghq9op9ie.jpg,https://res.cloudinary.com/dpysbryyk/image/upload/v1716958851/Milk/ABBOTT-GROW/Abbott-grow-2/tku9vp4qku3cowvprlhm.jpg]', 100.00, 'Abbott-grow-2', 1, 1, 1),
('Description 3', '[https://res.cloudinary.com/dpysbryyk/image/upload/v1716958852/Milk/ABBOTT-GROW/Abbott-grow-3/trif1lrrqlxkdj1m4rxb.png,https://res.cloudinary.com/dpysbryyk/image/upload/v1716958852/Milk/ABBOTT-GROW/Abbott-grow-3/g9epjdpkixc3xk0hmv1b.png,https://res.cloudinary.com/dpysbryyk/image/upload/v1716958852/Milk/ABBOTT-GROW/Abbott-grow-3/jirrtovhptzwc66jzfev.png]', 100.00, 'Abbott-grow-3', 1, 1, 1);

INSERT INTO `payment_method` (`method_name`)
VALUES
('CASH'),
('BANK');

INSERT INTO `inventory` (`quantity`, `product_id`)
VALUES
(0, 1),
(0, 2),
(0, 3);

INSERT INTO `role` (`role_name`)
VALUES
('MEMBER'),
('STAFF');

INSERT INTO `users` (`email`,`gender`, `full_name`, `image`, `password`, `phone`, `point`, `status`, `user_name`, `role_id`)
VALUES
('nguyenducson@gmail.com', 'Male', 'Đức Sơn', 'https://res.cloudinary.com/dpysbryyk/image/upload/v1717827115/Milk/UserDefault/dfzhxjcbnixmp8aybnge.jpg', '$2a$10$S.Kxv1Y7RNOqIg93UxZNXOCmytrxMSVMmfx5U/RHzDWiHeMG8PHra', '1111111111', 0, 1, 'nguyenducson', 1),
('nguyentiendung@gmail.com', 'Male', 'Tiến Dũng', 'https://res.cloudinary.com/dpysbryyk/image/upload/v1717827115/Milk/UserDefault/dfzhxjcbnixmp8aybnge.jpg', '$2a$10$X9tC9RYYvVSMOaqkjhTQz..rfQRaP5gh2q.zT9g.bQh4Cujyor/M2', '2222222222', 0, 1, 'nguyentiendung', 2);