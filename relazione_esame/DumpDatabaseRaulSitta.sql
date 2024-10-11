-- MySQL dump 10.13  Distrib 8.0.39, for Linux (x86_64)
--
-- Host: 127.0.0.1    Database: forumsdb
-- ------------------------------------------------------
-- Server version	8.0.39-0ubuntu0.22.04.1

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `CATEGORY`
--

DROP TABLE IF EXISTS `CATEGORY`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `CATEGORY` (
  `categoryID` bigint NOT NULL,
  `categoryName` varchar(100) DEFAULT NULL,
  `categoryDeleted` varchar(1) DEFAULT NULL,
  PRIMARY KEY (`categoryID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `CATEGORY`
--

LOCK TABLES `CATEGORY` WRITE;
/*!40000 ALTER TABLE `CATEGORY` DISABLE KEYS */;
INSERT INTO `CATEGORY` VALUES (0,'Vari','N'),(1,'Trasporti','N'),(2,'Scienza','N'),(3,'Cibo','N'),(4,'Scrittura','N'),(5,'Videogiochi','N'),(6,'Casa','N'),(7,'Informatica','N'),(8,'Social Media','N'),(9,'Finanza','N'),(10,'Video','N'),(11,'Musica','N'),(12,'Arte','N'),(13,'Fotografia','N'),(14,'Viaggi','N'),(15,'Lavoro','N'),(16,'Persone','N'),(17,'Cinema','N'),(18,'Sport','N'),(19,'Supporto','N');
/*!40000 ALTER TABLE `CATEGORY` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `COUNTER`
--

DROP TABLE IF EXISTS `COUNTER`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `COUNTER` (
  `counterID` varchar(20) NOT NULL,
  `counterValue` bigint DEFAULT NULL,
  PRIMARY KEY (`counterID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `COUNTER`
--

LOCK TABLES `COUNTER` WRITE;
/*!40000 ALTER TABLE `COUNTER` DISABLE KEYS */;
INSERT INTO `COUNTER` VALUES ('categoryID',0),('faqID',10),('mediaID',5),('postID',35),('topicID',7),('userID',7);
/*!40000 ALTER TABLE `COUNTER` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `FAQ`
--

DROP TABLE IF EXISTS `FAQ`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `FAQ` (
  `faqID` bigint NOT NULL,
  `faqQuestion` varchar(100) DEFAULT NULL,
  `faqAnswer` varchar(500) DEFAULT NULL,
  `faqCreationTimestamp` timestamp NULL DEFAULT NULL,
  `faqAuthorID` bigint DEFAULT NULL,
  `faqDeleted` varchar(1) DEFAULT NULL,
  PRIMARY KEY (`faqID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `FAQ`
--

LOCK TABLES `FAQ` WRITE;
/*!40000 ALTER TABLE `FAQ` DISABLE KEYS */;
INSERT INTO `FAQ` VALUES (1,'Come posso registrarmi agli eventi?','Per registrarti agli eventi, visita il nostro sito web e compila il modulo di registrazione disponibile nella sezione eventi.','2024-09-21 12:37:52',1,'N'),(2,'Cosa devo portare con me agli eventi?','Si prega di portare un documento d\'identità valido e la conferma della registrazione. Alcuni eventi possono richiedere ulteriori accreditamenti.','2024-09-22 08:15:30',1,'N'),(3,'C\'è un codice di abbigliamento per gli eventi?','Il codice di abbigliamento può variare a seconda della natura dell\'evento. Si raccomanda un abbigliamento consono agli standard dell\'evento specifico.','2024-09-23 10:42:10',1,'N'),(4,'Come posso annullare la mia registrazione a un evento?','Per annullare la registrazione, contatta il nostro supporto clienti tramite il sito web o utilizza il link fornito nella email di conferma.','2024-09-24 14:55:45',1,'N'),(5,'Ci saranno dei rimborsi se l\'evento viene cancellato?','In caso di cancellazione di un evento, tutti i partecipanti registrati riceveranno un rimborso completo. I dettagli specifici sono disponibili nella politica di rimborso sull\'evento.','2024-09-25 16:20:55',1,'N'),(6,'Come posso sapere se un evento è adatto ai bambini?','Controlla la descrizione dell\'evento sul nostro sito. Gli eventi adatti ai bambini saranno chiaramente indicati.','2024-09-26 17:00:00',1,'N'),(7,'È possibile portare cibo e bevande agli eventi?','La politica riguardo cibo e bevande varia in base alla location e al tipo di evento. Si prega di consultare le FAQ specifiche dell\'evento o contattare l\'organizzazione.','2024-09-27 18:30:00',1,'N'),(8,'Quali misure di sicurezza sono in atto durante gli eventi?','La sicurezza è la nostra priorità. Implementiamo controlli di sicurezza rigorosi, inclusi controlli all\'ingresso e sorveglianza durante l\'evento.','2024-09-28 06:45:00',1,'N'),(9,'Posso cambiare l\'evento per cui mi sono registrato?','I cambiamenti sono possibili fino a 48 ore prima dell\'evento, a seconda della disponibilità. Contatta il supporto per assistenza.','2024-09-29 07:15:00',1,'N'),(10,'Ci sono vantaggi per i membri che partecipano a più eventi?','I membri registrati che partecipano a più eventi possono beneficiare di sconti, accesso prioritario e offerte esclusive.','2024-09-30 08:00:00',1,'N');
/*!40000 ALTER TABLE `FAQ` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `MEDIA`
--

DROP TABLE IF EXISTS `MEDIA`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `MEDIA` (
  `mediaID` bigint NOT NULL,
  `mediaPath` varchar(500) DEFAULT NULL,
  `mediaCreationTimestamp` timestamp NULL DEFAULT NULL,
  `mediaUploaderID` bigint DEFAULT NULL,
  `mediaPostID` bigint DEFAULT NULL,
  `mediaDeleted` varchar(1) DEFAULT NULL,
  PRIMARY KEY (`mediaID`),
  KEY `mediaUploaderID_idx` (`mediaUploaderID`),
  KEY `mediaPostID_idx` (`mediaPostID`),
  CONSTRAINT `mediaPostID` FOREIGN KEY (`mediaPostID`) REFERENCES `POST` (`postID`),
  CONSTRAINT `mediaUploaderID` FOREIGN KEY (`mediaUploaderID`) REFERENCES `USER` (`userID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `MEDIA`
--

LOCK TABLES `MEDIA` WRITE;
/*!40000 ALTER TABLE `MEDIA` DISABLE KEYS */;
/*!40000 ALTER TABLE `MEDIA` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `POST`
--

DROP TABLE IF EXISTS `POST`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `POST` (
  `postID` bigint NOT NULL,
  `postContent` varchar(10000) DEFAULT NULL,
  `postCreationTimestamp` timestamp NULL DEFAULT NULL,
  `postAuthorID` bigint DEFAULT NULL,
  `postTopicID` bigint DEFAULT NULL,
  `postDeleted` varchar(1) DEFAULT NULL,
  `postEdited` varchar(1) DEFAULT NULL,
  PRIMARY KEY (`postID`),
  KEY `authorID_idx` (`postAuthorID`),
  KEY `post_topicID_idx` (`postTopicID`),
  CONSTRAINT `postAuthorID` FOREIGN KEY (`postAuthorID`) REFERENCES `USER` (`userID`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `postTopicID` FOREIGN KEY (`postTopicID`) REFERENCES `TOPIC` (`topicID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `POST`
--

LOCK TABLES `POST` WRITE;
/*!40000 ALTER TABLE `POST` DISABLE KEYS */;
INSERT INTO `POST` VALUES (1,'Ciao a tutti! Qualcuno ha dettagli su cosa aspettarsi dal Festival Gastronomico Autunnale di quest\'anno? Spero di parteciparvi per la prima volta!','2024-10-08 18:40:37',2,1,'N','N'),(2,'Ciao! Sono stato all\'evento l\'anno scorso. È un\'ottima occasione per provare piatti innovativi da chef locali e internazionali. Hanno anche delle sessioni di cucina dal vivo.','2024-10-08 18:41:55',3,1,'N','N'),(3,'Fantastico, grazie! Sai se ci saranno anche degustazioni di vini o altri eventi speciali legati alle bevande?','2024-10-08 18:42:41',2,1,'N','N'),(4,'Sì, l\'anno scorso c\'era una vasta selezione di vini locali e birre artigianali. Immagino che quest\'anno ripeteranno, vista la popolarità.','2024-10-08 18:43:10',3,1,'N','N'),(5,'Ottimo! E per quanto riguarda i biglietti, è meglio acquistarli in anticipo? Mi preoccupa che possano esaurirsi rapidamente.','2024-10-08 18:44:00',2,1,'N','N'),(6,'Decisamente sì, i biglietti vanno a ruba, soprattutto per le serate tematiche. Consiglio di prenotarli non appena possibile per evitare sorprese.','2024-10-08 18:44:13',3,1,'N','N'),(7,'Grazie mille per le info! Non vedo l\'ora di partecipare e di vivere questa esperienza culinaria. Ci vediamo là!','2024-10-08 18:44:27',2,1,'N','N'),(8,'Salve! Qualcuno ha partecipato agli incontri con gli artisti al Festival delle Arti Primaverile lo scorso anno? Vorrei sapere come sono organizzati.','2024-10-08 18:46:26',4,2,'N','N'),(9,'Ciao! Sì, ho partecipato a un paio di incontri. Gli artisti erano molto accessibili e c\'erano anche sessioni di Q&A dopo le presentazioni. Molto stimolante!','2024-10-08 18:46:39',2,2,'N','N'),(10,'Fantastico, grazie per la risposta! Sai se è possibile acquistare opere direttamente dagli artisti durante questi incontri?','2024-10-08 18:47:06',4,2,'N','N'),(11,'Assolutamente, molti artisti portano alcune delle loro opere e è possibile acquistarle. Alcuni accettano anche commissioni personalizzate sul posto.','2024-10-08 18:47:23',2,2,'N','N'),(12,'Ottimo! Un\'altra domanda: ci sono workshop pratici per principianti? Sono completamente nuovo di questo.','2024-10-08 18:47:38',4,2,'N','N'),(13,'Sì, ci sono diversi workshop, da quelli per principianti a quelli più avanzati. Ti consiglio di guardare il programma sul sito ufficiale e prenotare in anticipo, perché i posti tendono a esaurirsi velocemente.','2024-10-08 18:47:57',2,2,'N','N'),(14,'Perfetto, grazie mille per le informazioni! Non vedo l\'ora di partecipare e imparare di più.','2024-10-08 18:48:12',4,2,'N','N'),(15,'Ciao a tutti! Qualcuno sa quali film in anteprima saranno presentati al Festival Internazionale del Cinema quest\'anno?','2024-10-08 18:54:20',5,3,'N','N'),(16,'Salve! L\'organizzazione ha annunciato che ci saranno diverse anteprime internazionali, inclusi alcuni candidati agli Oscar. Non perdere \"Shadows in Time\", è molto atteso!','2024-10-08 18:54:34',2,3,'N','N'),(17,'Inoltre, ho sentito che ci saranno ospiti speciali. Pare che l\'attrice Julia Starness farà parte della giuria e terrà anche una masterclass.','2024-10-08 18:54:52',4,3,'N','N'),(18,'Wow, sarebbe fantastico vederla di persona! Sapete se ci sarà la possibilità di partecipare a sessioni di Q&A con gli attori?','2024-10-08 18:55:09',5,3,'N','N'),(19,'Sì, ci saranno diverse sessioni di Q&A dopo le proiezioni dei film. Ti consiglio di controllare il programma e prenotare in anticipo, perché i biglietti vanno a ruba!','2024-10-08 18:55:27',2,3,'N','N'),(20,'E non dimenticate le retrospettive. Quest\'anno ci sarà una dedicata a Alfred Hitchcock. Saranno proiettati alcuni dei suoi film più iconici in versione restaurata.','2024-10-08 18:55:42',4,3,'N','N'),(21,'Grazie mille per tutte queste informazioni! Sembra che sarà un\'edizione imperdibile. Non vedo l\'ora di essere lì.','2024-10-08 18:56:05',5,3,'N','N'),(22,'Ciao a tutti! Qualcuno ha già visto l\'agenda del Tech Global Summit 2024? Sembra che ci saranno alcune sessioni incredibili su AI e computazione quantistica.','2024-10-08 19:03:04',4,4,'N','N'),(23,'Sì, ho dato un\'occhiata! Sono particolarmente interessato al panel sulla sicurezza informatica con esperti da tutto il mondo. Qualcuno sa se ci sarà una sessione di Q&A?','2024-10-08 19:03:22',5,4,'N','N'),(24,'Confermo che ci sarà una sessione di Q&A dopo ogni panel. Sto aspettando con impazienza la dimostrazione di realtà virtuale. L\'anno scorso è stata la mia parte preferita!','2024-10-08 19:03:40',2,4,'N','N'),(25,'Qualcuno sa come funzionano le registrazioni per i workshop? Non voglio perdermi quello su blockchain e criptovalute.','2024-10-08 19:03:56',4,4,'N','N'),(26,'Di solito, devi registrarti attraverso il sito ufficiale del summit. Consiglio di farlo appena possibile perché i posti tendono a esaurirsi velocemente, specialmente per i workshop popolari.','2024-10-08 19:04:06',5,4,'N','N'),(27,'Per chi è alle prime armi, c\'è anche una app del summit che vi consiglio di scaricare. Ti aiuta a navigare il programma e anche a connetterti con altri partecipanti.','2024-10-08 19:04:41',2,4,'N','N'),(28,'Ottimo, grazie mille a tutti per le informazioni! Non vedo l\'ora di partecipare e di scoprire le ultime innovazioni. Ci vediamo là!\r\n\r\n','2024-10-08 19:04:57',4,4,'N','N'),(29,'Ciao a tutti, sono in una situazione un po\' frustrante e spero qualcuno qui possa aiutarmi. Sto cercando di registrarmi per il Summit Virtuale di Astrofisica 2024, un evento che ho atteso con impazienza per mesi, ma sto incontrando continui problemi tecnici che mi impediscono di completare la registrazione. Ogni volta che inserisco i miei dati e clicco su \'Invia\', la pagina carica per un po\' e poi ricevo un messaggio di errore che dice \'Impossibile elaborare la tua richiesta al momento\'. Inizialmente ho pensato potesse trattarsi di un problema temporaneo del loro server, quindi ho atteso un\'ora e ho riprovato, ma il problema persisteva.\r\n\r\nHo provato poi a cambiare browser, passando da Chrome a Firefox e infine a Safari, sperando che potesse dipendere da un problema di compatibilità del browser. Purtroppo, questo non ha cambiato nulla. Ho anche provato a svuotare la cache e i cookie di ciascun browser prima di tentare nuovamente, ma senza alcun successo. Ho controllato la mia connessione internet per escludere che il problema fosse dalla mia parte, ma tutto sembra funzionare perfettamente. Ho anche disattivato qualsiasi ad blocker e software di sicurezza che potrebbero interferire con la registrazione, ma niente da fare.\r\n\r\nHo contattato alcuni colleghi che hanno intenzione di partecipare all\'evento, e alcuni di loro sono riusciti a registrarsi senza problemi, mentre altri hanno riscontrato lo stesso mio problema. Questo mi fa pensare che il problema potrebbe essere intermittente o dipendere da particolari condizioni che non sono ancora state identificate dai tecnici del sito.\r\n\r\nHo inviato una email dettagliata al supporto tecnico dell\'evento, descrivendo il problema e tutte le soluzioni che ho tentato fino ad ora, ma non ho ancora ricevuto risposta. Sono preoccupato che il tempo per registrarsi possa esaurirsi e che possa perdere la possibilità di partecipare a questo importante evento. Se qualcuno ha avuto un\'esperienza simile o ha dei suggerimenti su come risolvere il problema, vi sarei molto grato se poteste condividerli. Grazie in anticipo per qualsiasi aiuto o consiglio che potrete fornire!','2024-10-08 19:08:39',6,5,'N','N'),(30,'Ciao. Un suggerimento che potrebbe funzionare: prova a registrarti usando il tuo smartphone o un altro dispositivo. A volte, questi problemi sono specifici per alcuni dispositivi o configurazioni. Se il problema persiste, potrebbe essere utile contattare direttamente il supporto dell\'evento tramite telefono, se disponibile, per una risoluzione più rapida. Spero che questo ti aiuti e che possa registrarti al Summit!','2024-10-08 19:09:48',1,5,'N','N'),(31,'Fantastico, ha funzionato! Ho provato a registrarmi usando il mio smartphone come suggerito e sono riuscito a completare la registrazione senza intoppi. Grazie mille per il consiglio pratico, non avrei mai pensato di provare con un altro dispositivo. Non vedo l\'ora di partecipare al Summit!','2024-10-08 19:10:44',6,5,'N','N'),(32,'Ciao a tutti, scrivo perché ho un dubbio riguardo al prossimo Gran Fondo che si terrà nella nostra regione il mese prossimo e preferirei restare anonimo per motivi personali. Ho letto le informazioni sull\'evento, ma ho delle perplessità specifiche riguardo alla difficoltà del percorso. Ho partecipato a diverse gare in passato, ma recentemente ho avuto problemi di salute che mi hanno costretto a ridurre l\'intensità del mio allenamento. Questo evento mi interessa molto perché passa attraverso alcuni dei paesaggi più belli della nostra area, e mi piacerebbe partecipare, ma sono preoccupato che il percorso possa essere troppo impegnativo per la mia attuale condizione fisica. Gli organizzatori hanno indicato che ci sono varie opzioni di percorso, ma non è chiaro quanto sia impegnativa la versione più breve. Inoltre, vorrei sapere se ci sono punti di assistenza medica lungo il percorso e quali sono le politiche di rimborso nel caso in cui non sia in grado di completare la gara. Apprezzerei molto se qualcuno che ha partecipato in passato o ha esperienza con questo tipo di eventi potesse darmi qualche consiglio su come affrontare la gara e su cosa aspettarmi. Grazie in anticipo per l\'aiuto, spero di poter partecipare e godermi l\'evento senza preoccupazioni.','2024-10-08 19:22:33',6,6,'N','N'),(33,'Ciao! Capisco perfettamente le tue preoccupazioni riguardo alla partecipazione al Gran Fondo, soprattutto considerando la tua recente condizione di salute. Per quanto riguarda la difficoltà del percorso, il Gran Fondo di solito offre diverse opzioni di percorso che variano in lunghezza e difficoltà. La versione più corta è generalmente progettata per essere accessibile a ciclisti di tutti i livelli, compresi coloro che potrebbero non essere in perfetta forma fisica o che stanno tornando da un infortunio.\r\n\r\nSul sito ufficiale dell\'evento, spesso si possono trovare mappe dettagliate e descrizioni dei percorsi che includono informazioni sul dislivello e sui tipi di terreno. Ti consiglierei di dare un\'occhiata a queste risorse, se non lo hai già fatto, per farti un\'idea più chiara della sfida fisica.\r\n\r\nPer quanto riguarda l\'assistenza medica, gli organizzatori di questi eventi sono generalmente molto preparati. Ci sono punti di assistenza medica e di soccorso dislocati lungo il percorso e, in caso di emergenza, le squadre di soccorso sono pronte a intervenire rapidamente. È sempre una buona idea controllare specificatamente per questo evento, magari contattando direttamente gli organizzatori per confermare.\r\n\r\nRiguardo alle politiche di rimborso, queste possono variare. Alcuni eventi offrono un rimborso completo o parziale fino a una certa data prima dell\'evento, mentre altri possono offrire un trasferimento dell\'iscrizione a un evento futuro in caso di non partecipazione per motivi medici. Ancora una volta, consiglio di verificare questa informazione direttamente sul sito dell\'evento o di chiedere specificatamente agli organizzatori.\r\n\r\nSpero che queste informazioni ti aiutino a prendere una decisione informata e che tu possa goderti l\'evento in sicurezza e felicità. Buona fortuna e spero che tutto vada per il meglio!','2024-10-08 19:24:04',6,6,'Y','N'),(34,'Ciao! Capisco perfettamente le tue preoccupazioni riguardo alla partecipazione al Gran Fondo, soprattutto considerando la tua recente condizione di salute. Per quanto riguarda la difficoltà del percorso, il Gran Fondo di solito offre diverse opzioni di percorso che variano in lunghezza e difficoltà. La versione più corta è generalmente progettata per essere accessibile a ciclisti di tutti i livelli, compresi coloro che potrebbero non essere in perfetta forma fisica o che stanno tornando da un infortunio.\r\n\r\nSul sito ufficiale dell\'evento, spesso si possono trovare mappe dettagliate e descrizioni dei percorsi che includono informazioni sul dislivello e sui tipi di terreno. Ti consiglierei di dare un\'occhiata a queste risorse, se non lo hai già fatto, per farti un\'idea più chiara della sfida fisica.\r\n\r\nPer quanto riguarda l\'assistenza medica, gli organizzatori di questi eventi sono generalmente molto preparati. Ci sono punti di assistenza medica e di soccorso dislocati lungo il percorso e, in caso di emergenza, le squadre di soccorso sono pronte a intervenire rapidamente. È sempre una buona idea controllare specificatamente per questo evento, magari contattando direttamente gli organizzatori per confermare.\r\n\r\nRiguardo alle politiche di rimborso, queste possono variare. Alcuni eventi offrono un rimborso completo o parziale fino a una certa data prima dell\'evento, mentre altri possono offrire un trasferimento dell\'iscrizione a un evento futuro in caso di non partecipazione per motivi medici. Ancora una volta, consiglio di verificare questa informazione direttamente sul sito dell\'evento o di chiedere specificatamente agli organizzatori.\r\n\r\nSpero che queste informazioni ti aiutino a prendere una decisione informata e che tu possa goderti l\'evento in sicurezza e felicità. Ti allego l\'immagine della locandina dell\'evento. Buona fortuna e spero che tutto vada per il meglio!','2024-10-08 19:24:22',1,6,'N','Y');
/*!40000 ALTER TABLE `POST` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `TOPIC`
--

DROP TABLE IF EXISTS `TOPIC`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `TOPIC` (
  `topicID` bigint NOT NULL,
  `topicTitle` varchar(50) DEFAULT NULL,
  `topicCreationTimestamp` timestamp NULL DEFAULT NULL,
  `topicAuthorID` bigint DEFAULT NULL,
  `topicCategoryID` bigint DEFAULT NULL,
  `topicAnonymous` varchar(1) DEFAULT 'N',
  `topicDeleted` varchar(1) DEFAULT NULL,
  PRIMARY KEY (`topicID`),
  KEY `fk_TOPIC_1_idx` (`topicAuthorID`),
  KEY `categoryID_idx` (`topicCategoryID`),
  CONSTRAINT `topicAuthorID` FOREIGN KEY (`topicAuthorID`) REFERENCES `USER` (`userID`) ON DELETE SET NULL ON UPDATE SET NULL,
  CONSTRAINT `topicCategoryID` FOREIGN KEY (`topicCategoryID`) REFERENCES `CATEGORY` (`categoryID`) ON DELETE SET NULL ON UPDATE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `TOPIC`
--

LOCK TABLES `TOPIC` WRITE;
/*!40000 ALTER TABLE `TOPIC` DISABLE KEYS */;
INSERT INTO `TOPIC` VALUES (1,'Chiarimenti su \"Festival Gastronomico Autunnale\"','2024-10-08 18:34:52',2,3,'N','N'),(2,'Incontri e Workshop al Festival delle Arti','2024-10-08 18:46:15',4,12,'N','N'),(3,'Novità e Ospiti Speciali al Festival Internazional','2024-10-08 18:54:04',5,17,'N','N'),(4,'Innovazioni al Tech Global Summit 2024','2024-10-08 18:59:37',4,7,'N','N'),(5,'Aiuto! Problema con la registrazione ad un evento','2024-10-08 19:08:15',6,19,'N','N'),(6,'Domanda su evento di ciclismo','2024-10-08 19:21:17',6,18,'Y','N');
/*!40000 ALTER TABLE `TOPIC` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `USER`
--

DROP TABLE IF EXISTS `USER`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `USER` (
  `userID` bigint NOT NULL,
  `userUsername` varchar(40) DEFAULT NULL,
  `userPassword` varchar(40) DEFAULT NULL,
  `userFirstname` varchar(50) DEFAULT NULL,
  `userSurname` varchar(50) DEFAULT NULL,
  `userEmail` varchar(100) DEFAULT NULL,
  `userBirthDate` date DEFAULT NULL,
  `userRegistrationTimestamp` timestamp NULL DEFAULT NULL,
  `userRole` varchar(20) DEFAULT NULL,
  `userProfilePicPath` varchar(200) DEFAULT NULL,
  `userDeleted` varchar(1) DEFAULT 'N',
  PRIMARY KEY (`userID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `USER`
--

LOCK TABLES `USER` WRITE;
/*!40000 ALTER TABLE `USER` DISABLE KEYS */;
INSERT INTO `USER` VALUES (1,'raul','raul','Raul','Sitta','raul.sitta@edu.unife.it','2002-11-26','2024-10-06 12:41:19','Admin','/images/defaultProfilePic.png','N'),(2,'mario.bianchi','mario.bianchi','Mario','Bianchi','mario.bianchi@gmail.com','2000-01-01','2024-10-08 18:32:16','User','/images/defaultProfilePic.png','N'),(3,'luigi.neri','luigi.neri','Luigi','Neri','luigi.neri@gmail.com','2000-01-01','2024-10-08 18:41:39','User','/images/defaultProfilePic.png','N'),(4,'giuseppe.verdi','giuseppe.verdi','Giuseppe','Verdi','giuseppe.verdi@gmail.com','2000-01-01','2024-10-08 18:45:32','User','/images/defaultProfilePic.png','N'),(5,'sandro.neri','sandro.neri','Sandro','Neri','sandro.neri@gmail.com','2002-01-01','2024-10-08 18:53:43','User','/images/defaultProfilePic.png','N'),(6,'simone.gialli','simone.gialli','Simone','Gialli','simone.gialli@gmail.com','2000-01-01','2024-10-08 19:07:41','User','/images/defaultProfilePic.png','N'),(7,'marcorossi00','marcorossi00','Marco','Rossi','marcorossi00@gmail.com','2000-01-01','2024-10-11 17:48:57','User','/Uploads/forums/users/7/profilePic/profilePic.png','N');
/*!40000 ALTER TABLE `USER` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2024-10-10 12:23:29
