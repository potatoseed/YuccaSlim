/*    ==Scripting Parameters==

    Source Server Version : SQL Server 2016 (13.0.4001)
    Source Database Engine Edition : Microsoft SQL Server Standard Edition
    Source Database Engine Type : Standalone SQL Server

    Target Server Version : SQL Server 2016
    Target Database Engine Edition : Microsoft SQL Server Standard Edition
    Target Database Engine Type : Standalone SQL Server
*/

USE [YS]
GO

/****** Object:  Table [dbo].[User]    Script Date: 2017/8/12 13:38:53 ******/
DROP TABLE [dbo].[User]
GO

/****** Object:  Table [dbo].[User]    Script Date: 2017/8/12 13:38:53 ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

CREATE TABLE [dbo].[User](
	[user_id] [uniqueidentifier] NOT NULL,
	[app_user_id] [int] NOT NULL,
	[user_name] [nvarchar](100) NULL,
	[first_name] [nvarchar](100) NULL,
	[last_name] [nvarchar](100) NULL,
	[email] [nvarchar](300) NULL,
	[weight] [decimal](4, 2) NULL,
	[age] [decimal](3, 1) NULL,
	[gender] [char](1) NULL,
	[update_time] [datetime] NULL,
	[update_by] [datetime] NULL,
 CONSTRAINT [PK_User] PRIMARY KEY CLUSTERED
(
	[user_id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO


USE [YS]
GO

/****** Object:  Table [dbo].[Food]    Script Date: 8/14/2017 10:16:24 AM ******/
DROP TABLE [dbo].[Food]
GO

/****** Object:  Table [dbo].[Food]    Script Date: 8/14/2017 10:16:24 AM ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

CREATE TABLE [dbo].[Food](
	[food_id] [uniqueidentifier] NOT NULL,
	[food_name] [nvarchar](100) NULL,
 CONSTRAINT [PK_Food] PRIMARY KEY CLUSTERED
(
	[food_id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
