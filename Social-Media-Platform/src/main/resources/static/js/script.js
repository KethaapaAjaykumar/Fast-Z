const API_BASE = '/api';
let currentUser = null;
let token = localStorage.getItem('jwt_token');

// Auth Check
document.addEventListener('DOMContentLoaded', () => {
    if (token) {
        showMain();
        loadPosts();
        loadUserData();
    } else {
        showAuth();
    }
});

function toggleAuth(type) {
    if (type === 'register') {
        document.getElementById('login-box').classList.add('hidden');
        document.getElementById('register-box').classList.remove('hidden');
    } else {
        document.getElementById('register-box').classList.add('hidden');
        document.getElementById('login-box').classList.remove('hidden');
    }
}

// Login
document.getElementById('login-form').addEventListener('submit', async (e) => {
    e.preventDefault();
    const email = document.getElementById('login-email').value;
    const password = document.getElementById('login-password').value;

    try {
        const response = await fetch(`${API_BASE}/auth/login`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ email, password })
        });

        if (response.ok) {
            const data = await response.json();
            localStorage.setItem('jwt_token', data.token);
            token = data.token;
            currentUser = data.user;
            showNotification('Welcome back to Fav-Z!');
            showMain();
            loadPosts();
            updateUI();
        } else {
            const error = await response.text();
            showNotification(error, 'error');
        }
    } catch (err) {
        showNotification('Connection failed', 'error');
    }
});

// Register
document.getElementById('register-form').addEventListener('submit', async (e) => {
    e.preventDefault();
    const request = {
        firstName: document.getElementById('reg-firstname').value,
        lastName: document.getElementById('reg-lastname').value,
        username: document.getElementById('reg-username').value,
        email: document.getElementById('reg-email').value,
        password: document.getElementById('reg-password').value
    };

    try {
        const response = await fetch(`${API_BASE}/auth/register`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(request)
        });

        const msg = await response.text();
        if (response.ok) {
            showNotification(msg || 'Registration successful!');
            toggleAuth('login');
        } else {
            showNotification(msg || 'Registration failed', 'error');
        }
    } catch (err) {
        showNotification('Connection failed. Please check if the server is running.', 'error');
    }
});

function showMain() {
    document.getElementById('auth-section').classList.add('hidden');
    document.getElementById('main-section').classList.remove('hidden');
}

function showAuth() {
    document.getElementById('main-section').classList.add('hidden');
    document.getElementById('auth-section').classList.remove('hidden');
}

function logout() {
    localStorage.removeItem('jwt_token');
    location.reload();
}

async function loadPosts() {
    try {
        const response = await fetch(`${API_BASE}/posts`, {
            headers: { 'Authorization': `Bearer ${token}` }
        });
        const data = await response.json();
        renderPosts(data.content);
    } catch (err) {
        console.error('Failed to load posts');
    }
}

// Image Preview
function previewImage() {
    const file = document.getElementById('post-image-file').files[0];
    if (file) {
        const reader = new FileReader();
        reader.onload = function (e) {
            document.getElementById('image-preview').src = e.target.result;
            document.getElementById('image-preview-container').classList.remove('hidden');
        }
        reader.readAsDataURL(file);
    }
}

function clearImage() {
    document.getElementById('post-image-file').value = '';
    document.getElementById('image-preview-container').classList.add('hidden');
}

async function createPost() {
    const content = document.getElementById('post-content').value;
    const imageFile = document.getElementById('post-image-file').files[0];

    if (!content && !imageFile) return;

    const formData = new FormData();
    formData.append('content', content);
    if (imageFile) {
        formData.append('image', imageFile);
    }

    try {
        const response = await fetch(`${API_BASE}/posts`, {
            method: 'POST',
            headers: { 'Authorization': `Bearer ${token}` },
            body: formData
        });

        if (response.ok) {
            document.getElementById('post-content').value = '';
            clearImage();
            loadPosts();
            refreshUserData(); // Refresh post count
            showNotification('Vibe shared successfully!');
        } else {
            const error = await response.text();
            showNotification(error, 'error');
        }
    } catch (err) {
        showNotification('Post creation failed', 'error');
    }
}

async function toggleLike(postId) {
    try {
        const response = await fetch(`${API_BASE}/likes/${postId}`, {
            method: 'POST',
            headers: { 'Authorization': `Bearer ${token}` }
        });
        if (response.ok) loadPosts();
    } catch (err) {
        console.error('Like failed');
    }
}

function renderPosts(posts) {
    const container = document.getElementById('posts-container');
    container.innerHTML = posts.map(post => `
        <div class="post-card glass">
            <div class="post-header">
                <div class="post-avatar">${post.username[0].toUpperCase()}</div>
                <div class="post-info">
                    <h4>${post.username}</h4>
                    <span>${new Date(post.createdAt).toLocaleString()}</span>
                </div>
            </div>
            <div class="post-content">${post.content}</div>
            ${post.imageUrl ? `<img src="${post.imageUrl}" class="post-img">` : ''}
            <div class="post-footer">
                <button onclick="toggleLike(${post.id})" class="action-btn">
                    <i class="fas fa-heart"></i> ${post.likeCount}
                </button>
                <button class="action-btn">
                    <i class="fas fa-comment"></i> ${post.comments ? post.comments.length : 0}
                </button>
            </div>
        </div>
    `).join('');
}

function showNotification(msg, type = 'success') {
    console.log(`Notification (${type}): ${msg}`);
    const el = document.getElementById('notification');
    if (!el) return;

    el.innerText = msg;
    el.style.background = type === 'error' ? '#ef4444' : 'linear-gradient(135deg, #8b5cf6, #ec4899)';
    el.style.display = 'block'; // Ensure it's not hidden by display: none
    el.classList.remove('hidden');

    // Auto-hide after 4 seconds
    setTimeout(() => {
        el.classList.add('hidden');
        el.style.display = 'none';
    }, 4000);
}

function updateUI() {
    if (currentUser) {
        document.getElementById('profile-name').innerText = `${currentUser.firstName} ${currentUser.lastName}`;
        document.getElementById('profile-username').innerText = `@${currentUser.username}`;
        document.getElementById('profile-avatar').innerText = currentUser.username[0].toUpperCase();
        document.getElementById('stat-posts').innerText = currentUser.postCount || 0;
        document.getElementById('stat-likes').innerText = currentUser.likeCount || 0;
    }
}

async function loadUserData() {
    // For now we get basic info from local storage or wait for refresh
    // In a real app, we'd have a /me endpoint
    if (!currentUser && token) {
        // Mock refresh for now by using the last saved data if available
        // Ideally: fetch(`${API_BASE}/auth/me`)
    }
    updateUI();
}

async function refreshUserData() {
    // Optional: Fetch updated user stats from backend
    // Since we don't have a /me endpoint yet, we just increment locally for better UX
    const postStat = document.getElementById('stat-posts');
    postStat.innerText = parseInt(postStat.innerText) + 1;
}
