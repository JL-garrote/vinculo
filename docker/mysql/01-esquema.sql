
/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `circulos` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `nombre` varchar(100) NOT NULL,
  `color` char(7) DEFAULT NULL,
  `cadencia_defecto_dias` smallint(5) unsigned DEFAULT NULL,
  `orden` smallint(6) NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  UNIQUE KEY `nombre` (`nombre`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `contactos` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `tratamiento` varchar(20) DEFAULT NULL,
  `nombre` varchar(100) NOT NULL,
  `apellidos` varchar(150) DEFAULT NULL,
  `foto_url` varchar(500) DEFAULT NULL,
  `empresa` varchar(200) DEFAULT NULL,
  `cargo` varchar(150) DEFAULT NULL,
  `ciudad` varchar(100) DEFAULT NULL,
  `pais` varchar(100) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `telefono` varchar(30) DEFAULT NULL,
  `linkedin_url` varchar(500) DEFAULT NULL,
  `canal_preferido` enum('whatsapp','llamada','email','presencial','videollamada') DEFAULT NULL,
  `circulo_id` int(10) unsigned DEFAULT NULL,
  `rol_relacion` varchar(50) DEFAULT NULL,
  `cadencia_dias` smallint(5) unsigned DEFAULT NULL,
  `prioritario` tinyint(1) NOT NULL DEFAULT 0,
  `intereses` varchar(500) DEFAULT NULL,
  `cumpleanos` date DEFAULT NULL,
  `familia` text DEFAULT NULL,
  `aficiones` text DEFAULT NULL,
  `hito_profesional` text DEFAULT NULL,
  `como_nos_conocimos` text DEFAULT NULL,
  `presentado_por_id` int(10) unsigned DEFAULT NULL,
  `proximo_paso` varchar(255) DEFAULT NULL,
  `proximo_paso_fecha` datetime DEFAULT NULL,
  `origen` enum('manual','google','linkedin','csv') NOT NULL DEFAULT 'manual',
  `id_externo` varchar(255) DEFAULT NULL,
  `archivado` tinyint(1) NOT NULL DEFAULT 0,
  `creado_en` datetime NOT NULL DEFAULT current_timestamp(),
  `actualizado_en` datetime NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_contactos_externo` (`origen`,`id_externo`),
  KEY `ix_contactos_circulo` (`circulo_id`),
  KEY `fk_contactos_presenta` (`presentado_por_id`),
  FULLTEXT KEY `ft_contactos` (`nombre`,`apellidos`,`empresa`,`cargo`,`ciudad`,`intereses`),
  CONSTRAINT `fk_contactos_circulo` FOREIGN KEY (`circulo_id`) REFERENCES `circulos` (`id`) ON DELETE SET NULL,
  CONSTRAINT `fk_contactos_presenta` FOREIGN KEY (`presentado_por_id`) REFERENCES `contactos` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `interacciones` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `contacto_id` int(10) unsigned NOT NULL,
  `contacto_relacionado_id` int(10) unsigned DEFAULT NULL,
  `tipo` enum('cafe','comida','reunion','llamada','videollamada','mensaje','email','evento','introduccion','otro') NOT NULL,
  `titulo` varchar(200) DEFAULT NULL,
  `fecha` datetime NOT NULL,
  `lugar` varchar(200) DEFAULT NULL,
  `resumen` text DEFAULT NULL,
  `temas` varchar(300) DEFAULT NULL,
  `es_borrador` tinyint(1) NOT NULL DEFAULT 0,
  `origen` enum('manual','google_calendar','gmail') NOT NULL DEFAULT 'manual',
  `id_externo` varchar(255) DEFAULT NULL,
  `creado_en` datetime NOT NULL DEFAULT current_timestamp(),
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_interacciones_externo` (`origen`,`id_externo`),
  KEY `ix_interacciones_contacto` (`contacto_id`,`fecha`),
  KEY `ix_interacciones_relacionado` (`contacto_relacionado_id`),
  KEY `ix_interacciones_fecha` (`fecha`),
  FULLTEXT KEY `ft_interacciones` (`titulo`,`lugar`,`resumen`,`temas`),
  CONSTRAINT `fk_int_contacto` FOREIGN KEY (`contacto_id`) REFERENCES `contactos` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_int_relacionado` FOREIGN KEY (`contacto_relacionado_id`) REFERENCES `contactos` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_unicode_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 */ /*!50003 TRIGGER trg_interacciones_ai AFTER INSERT ON interacciones
FOR EACH ROW
BEGIN
    IF NEW.es_borrador = FALSE THEN
        CALL sp_cumplir_recordatorios(NEW.contacto_id, NEW.fecha);
        IF NEW.contacto_relacionado_id IS NOT NULL THEN
            CALL sp_cumplir_recordatorios(NEW.contacto_relacionado_id, NEW.fecha);
        END IF;
    END IF;
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_unicode_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 */ /*!50003 TRIGGER trg_interacciones_au AFTER UPDATE ON interacciones
FOR EACH ROW
BEGIN
    IF OLD.es_borrador = TRUE AND NEW.es_borrador = FALSE THEN
        CALL sp_cumplir_recordatorios(NEW.contacto_id, NEW.fecha);
        IF NEW.contacto_relacionado_id IS NOT NULL THEN
            CALL sp_cumplir_recordatorios(NEW.contacto_relacionado_id, NEW.fecha);
        END IF;
    END IF;
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `notas` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `contacto_id` int(10) unsigned NOT NULL,
  `tipo` enum('general','oportunidad','preferencia','recurso') NOT NULL DEFAULT 'general',
  `titulo` varchar(200) DEFAULT NULL,
  `contenido` text NOT NULL,
  `fijada` tinyint(1) NOT NULL DEFAULT 0,
  `creado_en` datetime NOT NULL DEFAULT current_timestamp(),
  PRIMARY KEY (`id`),
  KEY `ix_notas_contacto` (`contacto_id`,`fijada`),
  CONSTRAINT `fk_notas_contacto` FOREIGN KEY (`contacto_id`) REFERENCES `contactos` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `perfil` (
  `id` tinyint(3) unsigned NOT NULL DEFAULT 1,
  `nombre` varchar(120) NOT NULL,
  `email` varchar(255) DEFAULT NULL,
  `avatar_url` varchar(500) DEFAULT NULL,
  `zona_horaria` varchar(64) NOT NULL DEFAULT 'Europe/Madrid',
  `objetivo_interacciones_semana` smallint(5) unsigned NOT NULL DEFAULT 10,
  `umbral_relacion_fuerte` tinyint(3) unsigned NOT NULL DEFAULT 70,
  `cadencia_por_defecto_dias` smallint(5) unsigned NOT NULL DEFAULT 30,
  `preferencias` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL CHECK (json_valid(`preferencias`)),
  `integraciones` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL CHECK (json_valid(`integraciones`)),
  `creado_en` datetime NOT NULL DEFAULT current_timestamp(),
  `actualizado_en` datetime NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  PRIMARY KEY (`id`),
  CONSTRAINT `chk_perfil_unico` CHECK (`id` = 1)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `pipeline_contactos` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `pipeline_id` int(10) unsigned NOT NULL,
  `contacto_id` int(10) unsigned NOT NULL,
  `etapa` enum('identificado','primer_contacto','reunion','seguimiento','aliado') NOT NULL DEFAULT 'identificado',
  `prioridad` enum('alta','media','baja') NOT NULL DEFAULT 'media',
  `siguiente_accion` varchar(255) DEFAULT NULL,
  `siguiente_accion_fecha` datetime DEFAULT NULL,
  `posicion` int(11) NOT NULL DEFAULT 0,
  `en_etapa_desde` datetime NOT NULL DEFAULT current_timestamp(),
  `creado_en` datetime NOT NULL DEFAULT current_timestamp(),
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_pipeline_contacto` (`pipeline_id`,`contacto_id`),
  KEY `ix_pc_etapa` (`pipeline_id`,`etapa`,`posicion`),
  KEY `fk_pc_contacto` (`contacto_id`),
  CONSTRAINT `fk_pc_contacto` FOREIGN KEY (`contacto_id`) REFERENCES `contactos` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_pc_pipeline` FOREIGN KEY (`pipeline_id`) REFERENCES `pipelines` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_unicode_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 */ /*!50003 TRIGGER trg_pipeline_contactos_bu BEFORE UPDATE ON pipeline_contactos
FOR EACH ROW
BEGIN
    IF NEW.etapa <> OLD.etapa THEN
        SET NEW.en_etapa_desde = NOW();
    END IF;
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `pipelines` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `nombre` varchar(150) NOT NULL,
  `objetivo` varchar(300) DEFAULT NULL,
  `activo` tinyint(1) NOT NULL DEFAULT 1,
  `creado_en` datetime NOT NULL DEFAULT current_timestamp(),
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `recordatorios` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `contacto_id` int(10) unsigned DEFAULT NULL,
  `titulo` varchar(200) NOT NULL,
  `descripcion` text DEFAULT NULL,
  `vence_en` datetime NOT NULL,
  `recurrencia_dias` smallint(5) unsigned DEFAULT NULL,
  `origen` enum('manual','asistente') NOT NULL DEFAULT 'manual',
  `estado` enum('pendiente','completado','descartado') NOT NULL DEFAULT 'pendiente',
  `completado_en` datetime DEFAULT NULL,
  `creado_en` datetime NOT NULL DEFAULT current_timestamp(),
  PRIMARY KEY (`id`),
  KEY `ix_recordatorios_estado` (`estado`,`vence_en`),
  KEY `ix_recordatorios_contacto` (`contacto_id`,`estado`),
  CONSTRAINT `fk_rec_contacto` FOREIGN KEY (`contacto_id`) REFERENCES `contactos` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
/*!50001 CREATE VIEW `v_contactos` AS SELECT
 1 AS `id`,
  1 AS `tratamiento`,
  1 AS `nombre`,
  1 AS `apellidos`,
  1 AS `foto_url`,
  1 AS `empresa`,
  1 AS `cargo`,
  1 AS `ciudad`,
  1 AS `pais`,
  1 AS `email`,
  1 AS `telefono`,
  1 AS `linkedin_url`,
  1 AS `canal_preferido`,
  1 AS `circulo_id`,
  1 AS `rol_relacion`,
  1 AS `cadencia_dias`,
  1 AS `prioritario`,
  1 AS `intereses`,
  1 AS `cumpleanos`,
  1 AS `familia`,
  1 AS `aficiones`,
  1 AS `hito_profesional`,
  1 AS `como_nos_conocimos`,
  1 AS `presentado_por_id`,
  1 AS `proximo_paso`,
  1 AS `proximo_paso_fecha`,
  1 AS `origen`,
  1 AS `id_externo`,
  1 AS `archivado`,
  1 AS `creado_en`,
  1 AS `actualizado_en`,
  1 AS `circulo`,
  1 AS `circulo_color`,
  1 AS `cadencia_efectiva`,
  1 AS `ultima_fecha`,
  1 AS `total_interacciones`,
  1 AS `dias_sin_contacto`,
  1 AS `nombre_completo`,
  1 AS `salud_pct`,
  1 AS `estado`,
  1 AS `proximo_toque_sugerido` */;
SET character_set_client = @saved_cs_client;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
/*!50001 CREATE VIEW `v_dashboard` AS SELECT
 1 AS `contactos_totales`,
  1 AS `pct_relaciones_fuertes`,
  1 AS `seguimientos_pendientes`,
  1 AS `frecuencia_media_dias`,
  1 AS `pct_red_al_dia`,
  1 AS `interacciones_semana`,
  1 AS `objetivo_semana` */;
SET character_set_client = @saved_cs_client;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
/*!50001 CREATE VIEW `v_pipeline` AS SELECT
 1 AS `id`,
  1 AS `pipeline_id`,
  1 AS `contacto_id`,
  1 AS `etapa`,
  1 AS `prioridad`,
  1 AS `siguiente_accion`,
  1 AS `siguiente_accion_fecha`,
  1 AS `posicion`,
  1 AS `en_etapa_desde`,
  1 AS `creado_en`,
  1 AS `pipeline`,
  1 AS `objetivo`,
  1 AS `nombre_completo`,
  1 AS `foto_url`,
  1 AS `empresa`,
  1 AS `cargo`,
  1 AS `salud_pct`,
  1 AS `estado` */;
SET character_set_client = @saved_cs_client;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
/*!50001 CREATE VIEW `v_proximos_cumpleanos` AS SELECT
 1 AS `contacto_id`,
  1 AS `contacto`,
  1 AS `proxima_fecha`,
  1 AS `dias_restantes`,
  1 AS `cumple_anios` */;
SET character_set_client = @saved_cs_client;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
/*!50001 CREATE VIEW `v_requieren_atencion` AS SELECT
 1 AS `id`,
  1 AS `tratamiento`,
  1 AS `nombre`,
  1 AS `apellidos`,
  1 AS `foto_url`,
  1 AS `empresa`,
  1 AS `cargo`,
  1 AS `ciudad`,
  1 AS `pais`,
  1 AS `email`,
  1 AS `telefono`,
  1 AS `linkedin_url`,
  1 AS `canal_preferido`,
  1 AS `circulo_id`,
  1 AS `rol_relacion`,
  1 AS `cadencia_dias`,
  1 AS `prioritario`,
  1 AS `intereses`,
  1 AS `cumpleanos`,
  1 AS `familia`,
  1 AS `aficiones`,
  1 AS `hito_profesional`,
  1 AS `como_nos_conocimos`,
  1 AS `presentado_por_id`,
  1 AS `proximo_paso`,
  1 AS `proximo_paso_fecha`,
  1 AS `origen`,
  1 AS `id_externo`,
  1 AS `archivado`,
  1 AS `creado_en`,
  1 AS `actualizado_en`,
  1 AS `circulo`,
  1 AS `circulo_color`,
  1 AS `cadencia_efectiva`,
  1 AS `ultima_fecha`,
  1 AS `total_interacciones`,
  1 AS `dias_sin_contacto`,
  1 AS `nombre_completo`,
  1 AS `salud_pct`,
  1 AS `estado`,
  1 AS `proximo_toque_sugerido` */;
SET character_set_client = @saved_cs_client;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
/*!50001 CREATE VIEW `v_salud_circulos` AS SELECT
 1 AS `id`,
  1 AS `nombre`,
  1 AS `color`,
  1 AS `orden`,
  1 AS `contactos`,
  1 AS `pct_al_dia` */;
SET character_set_client = @saved_cs_client;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,NO_ENGINE_SUBSTITUTION' */ ;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_unicode_ci */ ;
DELIMITER ;;
CREATE  PROCEDURE `sp_cumplir_recordatorios`(IN p_contacto INT UNSIGNED, IN p_fecha DATETIME)
BEGIN
    DECLARE v_max_id INT UNSIGNED DEFAULT 0;
    SELECT COALESCE(MAX(id), 0) INTO v_max_id FROM recordatorios;

    INSERT INTO recordatorios (contacto_id, titulo, descripcion, vence_en, recurrencia_dias, origen)
    SELECT contacto_id, titulo, descripcion,
           DATE_ADD(p_fecha, INTERVAL recurrencia_dias DAY), recurrencia_dias, origen
      FROM recordatorios
     WHERE contacto_id = p_contacto AND estado = 'pendiente'
       AND creado_en <= p_fecha AND recurrencia_dias IS NOT NULL;

    UPDATE recordatorios
       SET estado = 'completado', completado_en = NOW()
     WHERE contacto_id = p_contacto AND estado = 'pendiente'
       AND creado_en <= p_fecha
       AND id <= v_max_id;          -- no tocar los que se acaban de reprogramar
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50001 DROP VIEW IF EXISTS `v_contactos`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8mb4 */;
/*!50001 SET character_set_results     = utf8mb4 */;
/*!50001 SET collation_connection      = utf8mb4_unicode_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013  */
/*!50001 VIEW `v_contactos` AS with ult as (select `x`.`contacto_id` AS `contacto_id`,max(`x`.`fecha`) AS `ultima_fecha`,count(0) AS `total_interacciones` from (select `interacciones`.`contacto_id` AS `contacto_id`,`interacciones`.`fecha` AS `fecha` from `interacciones` where `interacciones`.`es_borrador` = 0 union all select `interacciones`.`contacto_relacionado_id` AS `contacto_relacionado_id`,`interacciones`.`fecha` AS `fecha` from `interacciones` where `interacciones`.`es_borrador` = 0 and `interacciones`.`contacto_relacionado_id` is not null) `x` group by `x`.`contacto_id`), base as (select `c`.`id` AS `id`,`c`.`tratamiento` AS `tratamiento`,`c`.`nombre` AS `nombre`,`c`.`apellidos` AS `apellidos`,`c`.`foto_url` AS `foto_url`,`c`.`empresa` AS `empresa`,`c`.`cargo` AS `cargo`,`c`.`ciudad` AS `ciudad`,`c`.`pais` AS `pais`,`c`.`email` AS `email`,`c`.`telefono` AS `telefono`,`c`.`linkedin_url` AS `linkedin_url`,`c`.`canal_preferido` AS `canal_preferido`,`c`.`circulo_id` AS `circulo_id`,`c`.`rol_relacion` AS `rol_relacion`,`c`.`cadencia_dias` AS `cadencia_dias`,`c`.`prioritario` AS `prioritario`,`c`.`intereses` AS `intereses`,`c`.`cumpleanos` AS `cumpleanos`,`c`.`familia` AS `familia`,`c`.`aficiones` AS `aficiones`,`c`.`hito_profesional` AS `hito_profesional`,`c`.`como_nos_conocimos` AS `como_nos_conocimos`,`c`.`presentado_por_id` AS `presentado_por_id`,`c`.`proximo_paso` AS `proximo_paso`,`c`.`proximo_paso_fecha` AS `proximo_paso_fecha`,`c`.`origen` AS `origen`,`c`.`id_externo` AS `id_externo`,`c`.`archivado` AS `archivado`,`c`.`creado_en` AS `creado_en`,`c`.`actualizado_en` AS `actualizado_en`,`ci`.`nombre` AS `circulo`,`ci`.`color` AS `circulo_color`,coalesce(`c`.`cadencia_dias`,`ci`.`cadencia_defecto_dias`,(select `perfil`.`cadencia_por_defecto_dias` from `perfil` where `perfil`.`id` = 1),30) AS `cadencia_efectiva`,`u`.`ultima_fecha` AS `ultima_fecha`,coalesce(`u`.`total_interacciones`,0) AS `total_interacciones`,to_days(curdate()) - to_days(cast(`u`.`ultima_fecha` as date)) AS `dias_sin_contacto` from ((`contactos` `c` left join `circulos` `ci` on(`ci`.`id` = `c`.`circulo_id`)) left join `ult` `u` on(`u`.`contacto_id` = `c`.`id`)) where `c`.`archivado` = 0)select `b`.`id` AS `id`,`b`.`tratamiento` AS `tratamiento`,`b`.`nombre` AS `nombre`,`b`.`apellidos` AS `apellidos`,`b`.`foto_url` AS `foto_url`,`b`.`empresa` AS `empresa`,`b`.`cargo` AS `cargo`,`b`.`ciudad` AS `ciudad`,`b`.`pais` AS `pais`,`b`.`email` AS `email`,`b`.`telefono` AS `telefono`,`b`.`linkedin_url` AS `linkedin_url`,`b`.`canal_preferido` AS `canal_preferido`,`b`.`circulo_id` AS `circulo_id`,`b`.`rol_relacion` AS `rol_relacion`,`b`.`cadencia_dias` AS `cadencia_dias`,`b`.`prioritario` AS `prioritario`,`b`.`intereses` AS `intereses`,`b`.`cumpleanos` AS `cumpleanos`,`b`.`familia` AS `familia`,`b`.`aficiones` AS `aficiones`,`b`.`hito_profesional` AS `hito_profesional`,`b`.`como_nos_conocimos` AS `como_nos_conocimos`,`b`.`presentado_por_id` AS `presentado_por_id`,`b`.`proximo_paso` AS `proximo_paso`,`b`.`proximo_paso_fecha` AS `proximo_paso_fecha`,`b`.`origen` AS `origen`,`b`.`id_externo` AS `id_externo`,`b`.`archivado` AS `archivado`,`b`.`creado_en` AS `creado_en`,`b`.`actualizado_en` AS `actualizado_en`,`b`.`circulo` AS `circulo`,`b`.`circulo_color` AS `circulo_color`,`b`.`cadencia_efectiva` AS `cadencia_efectiva`,`b`.`ultima_fecha` AS `ultima_fecha`,`b`.`total_interacciones` AS `total_interacciones`,`b`.`dias_sin_contacto` AS `dias_sin_contacto`,trim(concat_ws(' ',`b`.`tratamiento`,`b`.`nombre`,`b`.`apellidos`)) AS `nombre_completo`,case when `b`.`ultima_fecha` is null then 0 when `b`.`dias_sin_contacto` <= `b`.`cadencia_efectiva` then 100 else greatest(0,round(100 - (`b`.`dias_sin_contacto` - `b`.`cadencia_efectiva`) * 100.0 / `b`.`cadencia_efectiva`,0)) end AS `salud_pct`,case when `b`.`ultima_fecha` is null then 'sin_contacto' when `b`.`dias_sin_contacto` <= `b`.`cadencia_efectiva` * 0.8 then 'al_dia' when `b`.`dias_sin_contacto` <= `b`.`cadencia_efectiva` then 'toca_pronto' when `b`.`dias_sin_contacto` <= `b`.`cadencia_efectiva` * 2 then 'atrasado' else 'en_silencio' end AS `estado`,if(`b`.`ultima_fecha` is null,curdate(),cast(`b`.`ultima_fecha` as date) + interval `b`.`cadencia_efectiva` day) AS `proximo_toque_sugerido` from `base` `b` */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;
/*!50001 DROP VIEW IF EXISTS `v_dashboard`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8mb4 */;
/*!50001 SET character_set_results     = utf8mb4 */;
/*!50001 SET collation_connection      = utf8mb4_unicode_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013  */
/*!50001 VIEW `v_dashboard` AS with todas as (select `interacciones`.`contacto_id` AS `contacto_id`,`interacciones`.`fecha` AS `fecha` from `interacciones` where `interacciones`.`es_borrador` = 0 union all select `interacciones`.`contacto_relacionado_id` AS `contacto_relacionado_id`,`interacciones`.`fecha` AS `fecha` from `interacciones` where `interacciones`.`es_borrador` = 0 and `interacciones`.`contacto_relacionado_id` is not null), gaps as (select `todas`.`fecha` AS `fecha`,timestampdiff(HOUR,lag(`todas`.`fecha`,1) over ( partition by `todas`.`contacto_id` order by `todas`.`fecha`),`todas`.`fecha`) / 24.0 AS `gap` from `todas`)select (select count(0) from `v_contactos`) AS `contactos_totales`,(select coalesce(round(100.0 * sum(`v_contactos`.`salud_pct` >= (select `perfil`.`umbral_relacion_fuerte` from `perfil` where `perfil`.`id` = 1)) / nullif(count(0),0),0),0) from `v_contactos`) AS `pct_relaciones_fuertes`,(select count(0) from `recordatorios` where `recordatorios`.`estado` = 'pendiente') AS `seguimientos_pendientes`,(select round(avg(`gaps`.`gap`),0) from `gaps` where `gaps`.`gap` is not null and `gaps`.`fecha` >= curdate() - interval 180 day) AS `frecuencia_media_dias`,(select coalesce(round(100.0 * sum(`v_contactos`.`estado` in ('al_dia','toca_pronto')) / nullif(count(0),0),0),0) from `v_contactos`) AS `pct_red_al_dia`,(select count(0) from `interacciones` where `interacciones`.`es_borrador` = 0 and `interacciones`.`fecha` >= curdate() - interval weekday(curdate()) day) AS `interacciones_semana`,(select `perfil`.`objetivo_interacciones_semana` from `perfil` where `perfil`.`id` = 1) AS `objetivo_semana` */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;
/*!50001 DROP VIEW IF EXISTS `v_pipeline`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8mb4 */;
/*!50001 SET character_set_results     = utf8mb4 */;
/*!50001 SET collation_connection      = utf8mb4_unicode_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013  */
/*!50001 VIEW `v_pipeline` AS select `pc`.`id` AS `id`,`pc`.`pipeline_id` AS `pipeline_id`,`pc`.`contacto_id` AS `contacto_id`,`pc`.`etapa` AS `etapa`,`pc`.`prioridad` AS `prioridad`,`pc`.`siguiente_accion` AS `siguiente_accion`,`pc`.`siguiente_accion_fecha` AS `siguiente_accion_fecha`,`pc`.`posicion` AS `posicion`,`pc`.`en_etapa_desde` AS `en_etapa_desde`,`pc`.`creado_en` AS `creado_en`,`p`.`nombre` AS `pipeline`,`p`.`objetivo` AS `objetivo`,`v`.`nombre_completo` AS `nombre_completo`,`v`.`foto_url` AS `foto_url`,`v`.`empresa` AS `empresa`,`v`.`cargo` AS `cargo`,`v`.`salud_pct` AS `salud_pct`,`v`.`estado` AS `estado` from ((`pipeline_contactos` `pc` join `pipelines` `p` on(`p`.`id` = `pc`.`pipeline_id`)) join `v_contactos` `v` on(`v`.`id` = `pc`.`contacto_id`)) order by `pc`.`pipeline_id`,field(`pc`.`etapa`,'identificado','primer_contacto','reunion','seguimiento','aliado'),`pc`.`posicion` */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;
/*!50001 DROP VIEW IF EXISTS `v_proximos_cumpleanos`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8mb4 */;
/*!50001 SET character_set_results     = utf8mb4 */;
/*!50001 SET collation_connection      = utf8mb4_unicode_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013  */
/*!50001 VIEW `v_proximos_cumpleanos` AS with c as (select `contactos`.`id` AS `id`,`contactos`.`nombre` AS `nombre`,`contactos`.`apellidos` AS `apellidos`,`contactos`.`cumpleanos` AS `cumpleanos`,makedate(year(curdate()),1) + interval month(`contactos`.`cumpleanos`) - 1 month AS `m0`,makedate(year(curdate()) + 1,1) + interval month(`contactos`.`cumpleanos`) - 1 month AS `m1` from `contactos` where `contactos`.`cumpleanos` is not null and `contactos`.`archivado` = 0), d as (select `c`.`id` AS `id`,`c`.`nombre` AS `nombre`,`c`.`apellidos` AS `apellidos`,`c`.`cumpleanos` AS `cumpleanos`,`c`.`m0` AS `m0`,`c`.`m1` AS `m1`,`c`.`m0` + interval least(dayofmonth(`c`.`cumpleanos`),dayofmonth(last_day(`c`.`m0`))) - 1 day AS `d0`,`c`.`m1` + interval least(dayofmonth(`c`.`cumpleanos`),dayofmonth(last_day(`c`.`m1`))) - 1 day AS `d1` from `c`)select `d`.`id` AS `contacto_id`,trim(concat_ws(' ',`d`.`nombre`,`d`.`apellidos`)) AS `contacto`,if(`d`.`d0` < curdate(),`d`.`d1`,`d`.`d0`) AS `proxima_fecha`,to_days(if(`d`.`d0` < curdate(),`d`.`d1`,`d`.`d0`)) - to_days(curdate()) AS `dias_restantes`,if(year(`d`.`cumpleanos`) > 1904,year(if(`d`.`d0` < curdate(),`d`.`d1`,`d`.`d0`)) - year(`d`.`cumpleanos`),NULL) AS `cumple_anios` from `d` where to_days(if(`d`.`d0` < curdate(),`d`.`d1`,`d`.`d0`)) - to_days(curdate()) <= 30 order by to_days(if(`d`.`d0` < curdate(),`d`.`d1`,`d`.`d0`)) - to_days(curdate()) */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;
/*!50001 DROP VIEW IF EXISTS `v_requieren_atencion`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8mb4 */;
/*!50001 SET character_set_results     = utf8mb4 */;
/*!50001 SET collation_connection      = utf8mb4_unicode_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013  */
/*!50001 VIEW `v_requieren_atencion` AS select `v_contactos`.`id` AS `id`,`v_contactos`.`tratamiento` AS `tratamiento`,`v_contactos`.`nombre` AS `nombre`,`v_contactos`.`apellidos` AS `apellidos`,`v_contactos`.`foto_url` AS `foto_url`,`v_contactos`.`empresa` AS `empresa`,`v_contactos`.`cargo` AS `cargo`,`v_contactos`.`ciudad` AS `ciudad`,`v_contactos`.`pais` AS `pais`,`v_contactos`.`email` AS `email`,`v_contactos`.`telefono` AS `telefono`,`v_contactos`.`linkedin_url` AS `linkedin_url`,`v_contactos`.`canal_preferido` AS `canal_preferido`,`v_contactos`.`circulo_id` AS `circulo_id`,`v_contactos`.`rol_relacion` AS `rol_relacion`,`v_contactos`.`cadencia_dias` AS `cadencia_dias`,`v_contactos`.`prioritario` AS `prioritario`,`v_contactos`.`intereses` AS `intereses`,`v_contactos`.`cumpleanos` AS `cumpleanos`,`v_contactos`.`familia` AS `familia`,`v_contactos`.`aficiones` AS `aficiones`,`v_contactos`.`hito_profesional` AS `hito_profesional`,`v_contactos`.`como_nos_conocimos` AS `como_nos_conocimos`,`v_contactos`.`presentado_por_id` AS `presentado_por_id`,`v_contactos`.`proximo_paso` AS `proximo_paso`,`v_contactos`.`proximo_paso_fecha` AS `proximo_paso_fecha`,`v_contactos`.`origen` AS `origen`,`v_contactos`.`id_externo` AS `id_externo`,`v_contactos`.`archivado` AS `archivado`,`v_contactos`.`creado_en` AS `creado_en`,`v_contactos`.`actualizado_en` AS `actualizado_en`,`v_contactos`.`circulo` AS `circulo`,`v_contactos`.`circulo_color` AS `circulo_color`,`v_contactos`.`cadencia_efectiva` AS `cadencia_efectiva`,`v_contactos`.`ultima_fecha` AS `ultima_fecha`,`v_contactos`.`total_interacciones` AS `total_interacciones`,`v_contactos`.`dias_sin_contacto` AS `dias_sin_contacto`,`v_contactos`.`nombre_completo` AS `nombre_completo`,`v_contactos`.`salud_pct` AS `salud_pct`,`v_contactos`.`estado` AS `estado`,`v_contactos`.`proximo_toque_sugerido` AS `proximo_toque_sugerido` from `v_contactos` where `v_contactos`.`estado` in ('toca_pronto','atrasado','en_silencio','sin_contacto') or `v_contactos`.`id` in (select `recordatorios`.`contacto_id` from `recordatorios` where `recordatorios`.`estado` = 'pendiente' and `recordatorios`.`vence_en` <= current_timestamp() + interval 1 day) order by `v_contactos`.`prioritario` desc,`v_contactos`.`salud_pct` */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;
/*!50001 DROP VIEW IF EXISTS `v_salud_circulos`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8mb4 */;
/*!50001 SET character_set_results     = utf8mb4 */;
/*!50001 SET collation_connection      = utf8mb4_unicode_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013  */
/*!50001 VIEW `v_salud_circulos` AS select `ci`.`id` AS `id`,`ci`.`nombre` AS `nombre`,`ci`.`color` AS `color`,`ci`.`orden` AS `orden`,count(`v`.`id`) AS `contactos`,round(100.0 * sum(`v`.`estado` in ('al_dia','toca_pronto')) / nullif(count(`v`.`id`),0),0) AS `pct_al_dia` from (`circulos` `ci` left join `v_contactos` `v` on(`v`.`circulo_id` = `ci`.`id`)) group by `ci`.`id`,`ci`.`nombre`,`ci`.`color`,`ci`.`orden` order by `ci`.`orden` */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

