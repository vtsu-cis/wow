--------------------------------------------------------------------------
-- FILE     : connections.ads
-- SUBJECT  : Specification for connection handling package.
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
with Connection_Pool;

package Connections is

   task type Connection_Handler_Task is
      entry Launch
        (Client  : in GNAT.Sockets.Socket_Type;
         Number  : in Connection_Pool.Connection_Index);
   end Connection_Handler_Task;

end Connections;
