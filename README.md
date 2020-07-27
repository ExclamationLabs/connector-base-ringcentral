Update: Cannot base this connector off of public Java SDK; 
public SDK doesn't support SCIM and Paging Only Groups, which would need 
to use to CRUD Users and Groups.  We will have to integrate with the RESTful services ourselves.

PagingOnlyGroups do not seem to be listable or maintainable.  
So this connector will only support CRUD for Users.

In RingCentral, users can have multiple emails, phones and addresses.  But for IAM purposes
we only read and update a single one (first one in list).

RingCentral supports paging and the default page size seems to be 100.  But connector as of this
writing does not support paging.  Retrieving more than 100 users may not work.
