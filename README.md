Reg no: 12306781 Name:Kethapa Ajay Kumar


Fav-Z is a premium, full-stack social media platform built for the modern web. Featuring a sleek Glassmorphism UI, real-time interactions, and a "Vibe-centric" ecosystem, it redefines how you share your moments.



✨ Key Features
🚀 Seamless Authentication: Secure login and registration with JWT (JSON Web Tokens) and Email Verification (via Gmail SMTP).
📸 Image Uploads: Share your vibe with high-quality photo uploads directly from your device.
🌌 Premium UI: A stunning dark-mode interface featuring neon accents, frosted glass effects, and smooth micro-animations.
📈 User Statistics: Track your growth with real-time Post Counts and total Vibes (Likes) received.
💌 Vibe Interactions: Like and comment on posts to engage with the community.
🔍 Search Engine: Discover content and users across the platform effortlessly.
🛠️ Tech Stack
Backend:

Java 17+ with Spring Boot 3
Spring Security (Custom JWT Filter)
Spring Data JPA (Hibernate)
Supabase (PostgreSQL)
Java Mail Sender (Gmail API)
Frontend:

Vanilla JavaScript (Modular API architecture)
CSS3 (Advanced Glassmorphism & Flex/Grid layouts)
HTML5 (Semantic structure)
🚀 Getting Started
1. Prerequisites
JDK 17 or higher
Maven (included via 
mvnw
)
A Supabase account (PostgreSQL)
2. Configuration
Create a 
src/main/resources/application.properties
 file and configure your environment:

properties
# Database (Supabase)
spring.datasource.url=jdbc:postgresql://your-supabase-url:6543/postgres?prepareThreshold=0
spring.datasource.username=your-username
spring.datasource.password=your-password
# Email (Gmail SMTP)
spring.mail.username=your-email@gmail.com
spring.mail.password=your-16-digit-app-password
# Security
jwt.secret=your-64-character-hex-secret
3. Installation & Run
powershell
# Clone the repository
git clone https://github.com/yourusername/Fav-Z.git
# Enter the directory
cd Social-Media-Platform
# Run the app
.\mvnw.cmd spring-boot:run
Access the app at: http://localhost:8080

📸 Screen Shots
(Add your generated images here once you push to GitHub!)


