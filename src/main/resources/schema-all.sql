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
