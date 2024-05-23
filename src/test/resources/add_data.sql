insert into writers (id, first_name, last_name, status)
values (1, 'Name', 'Last Name', 'ACTIVE')
on conflict do nothing;

insert into posts (id, title, content, status, writer_id)
values (1, 'Title', 'Content', 'ACTIVE', 1)
on conflict do nothing;

insert into labels (id, name, status, post_id)
values (1, 'Label', 'ACTIVE', 1)
on conflict do nothing;