USE [master] 
GO
CREATE LOGIN [jpkloader] WITH PASSWORD=N'jpkloader', DEFAULT_DATABASE=[jpkloader], CHECK_EXPIRATION=OFF, CHECK_POLICY=ON
GO


USE [jpkloader] 
GO 
CREATE USER [jpkloader] FOR LOGIN [jpkloader]
GO 
EXEC sp_addrolemember N'db_owner', N'jpkloader'
GO
