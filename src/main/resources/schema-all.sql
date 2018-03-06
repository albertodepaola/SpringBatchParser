DROP TABLE people IF EXISTS;

CREATE TABLE logline  (
    logline_id BIGINT IDENTITY NOT NULL PRIMARY KEY,
    type VARCHAR(20),
    sellerName VARCHAR(200),    
	cpf VARCHAR(20),           
	sallary VARCHAR(20),       
	               
	cnpj VARCHAR(20),          
	clientName VARCHAR(200),    
	businessType VARCHAR(200),  
	               
	saleId BIGINT,          
	saleItems VARCHAR(2000),     
	saleSellerName VARCHAR(200)
    
    
);

CREATE TABLE seller  (
    seller_id BIGINT IDENTITY NOT NULL PRIMARY KEY,
    type VARCHAR(20),
    sellerName VARCHAR(200),    
	cpf VARCHAR(20),           
	sallary VARCHAR(20)
    
    
);

CREATE TABLE client  (
    logline_id BIGINT IDENTITY NOT NULL PRIMARY KEY,
    type VARCHAR(20),             
	cnpj VARCHAR(20),          
	clientName VARCHAR(200),    
	businessType VARCHAR(200),  
   
    
);

CREATE TABLE sale  (
    sale_id BIGINT IDENTITY NOT NULL PRIMARY KEY,
    type VARCHAR(20),
    saleId VARCHAR(20),          
	saleItems VARCHAR(2000),     
	saleSellerName VARCHAR(200),
	saleAmount DECIMAL(20,2)
    
);

CREATE SEQUENCE sale_sequence AS BIGINT;

CREATE TABLE saledetail  (
    saledetail_id BIGINT IDENTITY NOT NULL PRIMARY KEY,
    sale_id BIGINT,
    itemId BIGINT,          
	quantity BIGINT,     
	price BIGINT
    
);
