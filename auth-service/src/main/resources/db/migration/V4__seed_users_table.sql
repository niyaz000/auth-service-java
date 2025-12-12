INSERT INTO
    tenants (
        public_id,
        name,
        status,
        email,
        phone_number,
        country_code,
        email_verified_at,
        phone_number_verified_at,
        created_by,
        updated_by,
        request_id
    )
VALUES
    (
        'ten_pQ4nZ82LbH0yR',
        'default',
        'VERIFIED',
        'nk@user-service.in',
        '1876543210',
        'IN',
        NOW(),
        NOW(),
        1,
        1,
        gen_random_uuid()
    ) ON CONFLICT DO NOTHING;

INSERT INTO
    users (
        id,
        public_id,
        tenant_id,
        first_name,
        last_name,
        email,
        email_verified_at,
        phone_number,
        phone_number_verified_at,
        status,
        created_by,
        updated_by,
        request_id
    )
VALUES
    (
        1,
        'usr_A1b2C3d4E5fG6',
        (
            SELECT
                id
            FROM
                tenants
            WHERE
                name = 'default'
        ),
        'System',
        'User',
        'nk@user-service.in',
        NOW(),
        '1876543210',
        NOW(),
        'VERIFIED',
        1,
        1,
        gen_random_uuid()
    ) ON CONFLICT DO NOTHING;