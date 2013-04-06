import sys

NAME    = sys.argv[1]
EMAIL   = sys.argv[2]
TYPE    = sys.argv[3]
MSG     = sys.argv[4]

import libgmail
import string

ACCOUNT = 'andysib@gmail.com'
PASS = 'mmmooo'

account = libgmail.GmailAccount(ACCOUNT, PASS)
account.login()

msg = 'You have received feedback from the /validate/feedback.html page:'
msg += '\nName: %s' % NAME
msg += '\nEmail: %s' % EMAIL
msg += '\nType: %s' % TYPE
msg += '\nMessage: %s' % MSG

gmessage = libgmail.GmailComposedMessage('wowFeedBack@gmail.com', 'Feedback Received', msg)

if account.sendMessage(gmessage):
	print 'E-mail sent successfully.'
else:
	print 'Could not send e-mail to ', EMAIL
