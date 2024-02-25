-- --------------------------------------------------------
-- Servidor:                     localhost
-- Versão do servidor:           5.6.13 - MySQL Community Server (GPL)
-- OS do Servidor:               Win32
-- HeidiSQL Versão:              11.3.0.6295
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;


-- Copiando estrutura do banco de dados para bd_caixa
CREATE DATABASE IF NOT EXISTS `bd_caixa` /*!40100 DEFAULT CHARACTER SET latin1 */;
USE `bd_caixa`;

-- Copiando estrutura para tabela bd_caixa.tb01_produto
CREATE TABLE IF NOT EXISTS `tb01_produto` (
  `tb01_cod_prod` bigint(20) NOT NULL,
  `tb01_descricao` varchar(100) DEFAULT NULL,
  `tb01_preco` double DEFAULT NULL,
  PRIMARY KEY (`tb01_cod_prod`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Exportação de dados foi desmarcado.

-- Copiando estrutura para tabela bd_caixa.tb02_nota_fiscal
CREATE TABLE IF NOT EXISTS `tb02_nota_fiscal` (
  `tb02_nro_cupom` int(11) NOT NULL,
  `tb02_data` date DEFAULT NULL,
  `tb02_hora` time DEFAULT NULL,
  `tb02_valor_total` double DEFAULT NULL,
  PRIMARY KEY (`tb02_nro_cupom`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Exportação de dados foi desmarcado.

-- Copiando estrutura para tabela bd_caixa.tb03_vendas
CREATE TABLE IF NOT EXISTS `tb03_vendas` (
  `tb03_id_compra` int(11) NOT NULL,
  `tb03_num_caixa` int(11) DEFAULT NULL,
  `tb03_cupom` int(11) NOT NULL,
  `tb03_cod_produto` bigint(20) DEFAULT NULL,
  `tb03_valor` double DEFAULT NULL,
  `tb03_qntde` int(11) DEFAULT NULL,
  `tb03_valor_total` int(11) DEFAULT NULL,
  PRIMARY KEY (`tb03_id_compra`),
  KEY `FK_tb03_vendas_tb01_produto` (`tb03_cod_produto`),
  KEY `FK_tb03_vendas_tb02_nota_fiscal` (`tb03_cupom`),
  CONSTRAINT `FK_tb03_vendas_tb01_produto` FOREIGN KEY (`tb03_cod_produto`) REFERENCES `tb01_produto` (`tb01_cod_prod`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `FK_tb03_vendas_tb02_nota_fiscal` FOREIGN KEY (`tb03_cupom`) REFERENCES `tb02_nota_fiscal` (`tb02_nro_cupom`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Exportação de dados foi desmarcado.

/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IFNULL(@OLD_FOREIGN_KEY_CHECKS, 1) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40111 SET SQL_NOTES=IFNULL(@OLD_SQL_NOTES, 1) */;
