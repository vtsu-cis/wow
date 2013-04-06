--------------------------------------------------------------------------
-- FILE     : connections.adb
-- SUBJECT  : Connection handling package.
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

package body Connections is

   task body Connection_Handler_Task is
      Client_Socket : GNAT.Sockets.Socket_Type;
      My_Number     : Connection_Pool.Connection_Index;
      
      procedure Interact_With_Client is
      begin
         null;
         
      -- Log all exceptions that reach this level.
      exception
         when others =>
            null;
      end Interact_With_Client;
      
   begin -- Connection_Handler_Task
      loop
         select
            accept Launch
              (Client  : in GNAT.Sockets.Socket_Type;
               Number  : in Connection_Pool.Connection_Index) do

               Client_Socket := Client;
               My_Number := Number;
            end Launch;
         or
            -- Something better than this will be needed eventually.
            terminate;
         end select;
         
         Interact_With_Client;
         GNAT.Sockets.Close_Socket(Client_Socket);
         Connection_Pool.Deactivate_Connection(My_Number);
      end loop;
   end Connection_Handler_Task;

end Connections;
