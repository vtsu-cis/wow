import sys

EMAIL = sys.argv[1]

import libgmail
import string

ACCOUNT = 'andysib@gmail.com'
PASS = 'mmmooo'

account = libgmail.GmailAccount(ACCOUNT, PASS)
account.login()

gmessage = libgmail.GmailComposedMessage(EMAIL, 'Entry Updated', 'Your entry was updated with the information you gave us. Thank you for your time.')

if account.sendMessage(gmessage):
	print 'E-mail sent successfully.'
else:
	print 'Could not send e-mail to ', EMAIL
