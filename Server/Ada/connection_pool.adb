--------------------------------------------------------------------------
-- FILE     : connection_pool.adb
-- SUBJECT  : Pool of connection handling tasks.
-- AUTHOR   : (C) Copyright 2008 by Vermont Technical College
--
-- LICENSE
--
-- This program is free software; you can redistribute it and/or modify it
-- under the terms of the GNU General Public License as published by the
-- Free Software Foundation; either version 2 of the License, or (at your
-- option) any later version.
--
-- This program is distributed in the hope that it will be useful, but
-- WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANT-
-- ABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public
-- License for more details.
--
-- You should have received a copy of the GNU General Public License along
-- with this program; if not, write to the Free Software Foundation, Inc.,
-- 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
--
-- TODO
--
--   + Finish me!
--
-- Please send comments and bug reports to
--
--      Summer of Software Engineering
--      Vermont Technical College
--      201 Lawrence Place
--      Williston, VT 05495
--      sose@vtc.edu, http://www.vtc.edu/sose
---------------------------------------------------------------------------
with Connections;

package body Connection_Pool is

   type Pool_Type is array(Connection_Index) of Connections.Connection_Handler_Task;
   type Pool_Flags_Type is array(Connection_Index) of Boolean;
   
   Handlers : Pool_Type;

   -- An object to manage the connections in the pool.
   protected Connection_Manager is
      entry Allocate_Handler(Handler_Number : out Connection_Index);
      procedure Release_Handler(Handler_Number : in Connection_Index);
      function Get_Count return Natural;
      function Get_Maximum_Count return Natural;
   private
      Allocated     : Pool_Flags_Type := (others => False);
      Count         : Natural := 0;
      Maximum_Count : Natural := 0;
   end Connection_Manager;


   protected body Connection_Manager is

      entry Allocate_Handler(Handler_Number : out Connection_Index)
         when Count < Max_Connection_Count is
      begin
         -- Some kind of round robin approach might be better (or maybe not).
         for I in Connection_Index loop
            if Allocated(I) = False then
               Allocated(I) := True;
               Count := Count + 1;
               if Count > Maximum_Count then
                  Maximum_Count := Count;
               end if;
               Handler_Number := I;
               return;
            end if;
         end loop;
      end Allocate_Handler;

      procedure Release_Handler(Handler_Number : in Connection_Index) is
      begin
         Allocated(Handler_Number) := False;
         Count := Count - 1;
      end Release_Handler;
      
      function Get_Count return Natural is
      begin
         return Count;
      end Get_Count;
      
      function Get_Maximum_Count return Natural is
      begin
         return Maximum_Count;
      end Get_Maximum_Count;

   end Connection_Manager;

   
   procedure Activate_Connection(Client : in GNAT.Sockets.Socket_Type) is
      Handler_Number : Connection_Index;
   begin
      Connection_Manager.Allocate_Handler(Handler_Number);
      Handlers(Handler_Number).Launch(Client, Handler_Number);
   end Activate_Connection;
   
   
   procedure Deactivate_Connection(Handler_Number : in Connection_Index) is
   begin
      Connection_Manager.Release_Handler(Handler_Number);
   end Deactivate_Connection;

end Connection_Pool;
