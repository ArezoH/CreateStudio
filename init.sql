-- ============================================
-- Creative Studio — Database Initialization
-- ============================================
-- This script runs automatically when the
-- PostgreSQL Docker container starts for the
-- first time.
-- ============================================

-- Grant permissions
GRANT ALL ON SCHEMA public TO studio_admin;

-- ============================================
-- Test Users (passwords are BCrypt-hashed)
-- ============================================
-- Password for both users: password123
-- BCrypt hash generated with strength 10

INSERT INTO users (id, email, username, password_hash, created_at, updated_at)
VALUES
  ('a1b2c3d4-e5f6-7890-abcd-ef1234567890', 'test@example.com', 'testuser',
   '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
   NOW(), NOW()),
  ('b2c3d4e5-f6a7-8901-bcde-f12345678901', 'user2@example.com', 'user2',
   '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
   NOW(), NOW())
ON CONFLICT DO NOTHING;

-- ============================================
-- Sample Dashboard for Test User 1
-- ============================================

INSERT INTO dashboards (id, user_id, name, grid_size, created_at, updated_at)
VALUES
  ('d1a2b3c4-e5f6-7890-abcd-ef1234567890',
   'a1b2c3d4-e5f6-7890-abcd-ef1234567890',
   'My Workspace', 40, NOW(), NOW())
ON CONFLICT DO NOTHING;

-- ============================================
-- Sample Widgets
-- ============================================

INSERT INTO widgets (id, dashboard_id, type, x, y, width, height, z_index, data, created_at, updated_at)
VALUES
  -- Content Editor
  ('w1000000-0000-0000-0000-000000000001',
   'd1a2b3c4-e5f6-7890-abcd-ef1234567890',
   'content-editor', 0, 0, 400, 300, 1,
   '{"content": "<p>Welcome to Creative Studio! Start editing here.</p>"}',
   NOW(), NOW()),

  -- Todo List
  ('w2000000-0000-0000-0000-000000000002',
   'd1a2b3c4-e5f6-7890-abcd-ef1234567890',
   'todo-list', 440, 0, 400, 300, 2,
   '{"todos": [{"id": "t1", "text": "Build REST API", "completed": true, "createdAt": "2026-02-15T10:00:00Z"}, {"id": "t2", "text": "Create Angular frontend", "completed": false, "createdAt": "2026-02-15T10:01:00Z"}, {"id": "t3", "text": "Add JWT authentication", "completed": true, "createdAt": "2026-02-15T10:02:00Z"}]}',
   NOW(), NOW()),

  -- Quick Notes
  ('w3000000-0000-0000-0000-000000000003',
   'd1a2b3c4-e5f6-7890-abcd-ef1234567890',
   'notes-widget', 0, 340, 400, 300, 3,
   '{"text": "Quick notes:\n- Meeting at 3pm\n- Review PR #42\n- Deploy to staging"}',
   NOW(), NOW())
ON CONFLICT DO NOTHING;