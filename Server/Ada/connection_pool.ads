--------------------------------------------------------------------------
-- FILE     : connection_pool.ads
-- SUBJECT  : Specification for package to handle the pool of connections.
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
with GNAT.Sockets;

package Connection_Pool is

   -- Maximum number of simultaneously active connections.
   Max_Connection_Count : constant := 8;
   subtype Connection_Index is Natural range 0 .. Max_Connection_Count - 1;

   -- Helper procedure to launch a task to handle the given connection.
   procedure Activate_Connection(Client : in GNAT.Sockets.Socket_Type);
   
   -- This should only be called from inside a connection handler task. 
   procedure Deactivate_Connection(Handler_Number : in Connection_Index);

end Connection_Pool;
