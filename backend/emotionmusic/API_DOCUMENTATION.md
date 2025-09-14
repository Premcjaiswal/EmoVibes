# EmoVibes Backend API Documentation

## Overview
EmoVibes is an emotion-aware music recommendation system that provides personalized music suggestions based on user emotions and preferences.

## Base URL
```
http://localhost:8080/api
```

## Authentication
Currently, all endpoints are open (no authentication required). This is configured for development purposes.

## API Endpoints

### 1. Emotion Management
**Base Path:** `/api/emotions`

| Method | Endpoint | Description | Request Body | Response |
|--------|----------|-------------|--------------|----------|
| GET | `/emotions` | Get all emotions | - | List of emotions |
| GET | `/emotions/{userId}` | Get emotions by user ID | - | List of emotions for user |
| POST | `/emotions` | Create new emotion | Emotion object | Created emotion |
| PUT | `/emotions/{id}` | Update emotion | Emotion object | Updated emotion |
| DELETE | `/emotions/{id}` | Delete emotion | - | 204 No Content |

**Emotion Object:**
```json
{
  "userId": "string",
  "emotionType": "string",
  "confidenceScore": 0.85,
  "inputType": "string",
  "meta": {}
}
```

### 2. Music Recommendations
**Base Path:** `/api/recommend`

| Method | Endpoint | Description | Response |
|--------|----------|-------------|----------|
| GET | `/recommend/{emotion}` | Get music recommendations by emotion | Formatted track list |
| GET | `/recommend/genres` | Get available music genres | List of genres |

**Supported Emotions:**
- `happy`, `joy` → Pop music
- `sad` → Acoustic music  
- `angry` → Metal music
- `calm`, `relaxed` → Chill music
- `energetic`, `excited` → EDM music

**Example Response:**
```json
{
  "emotion": "happy",
  "total_tracks": 100,
  "tracks": [
    {
      "id": "track_id",
      "name": "Song Name",
      "artist": "Artist Name",
      "album": "Album Name",
      "preview_url": "https://p.scdn.co/mp3-preview/...",
      "spotify_url": "https://open.spotify.com/track/..."
    }
  ]
}
```

### 3. Playlist Management
**Base Path:** `/api/playlists`

| Method | Endpoint | Description | Request Body | Response |
|--------|----------|-------------|--------------|----------|
| GET | `/playlists` | Get all playlists | - | List of playlists |
| GET | `/playlists/user/{userId}` | Get playlists by user ID | - | List of user's playlists |
| POST | `/playlists` | Create new playlist | Playlist object | Created playlist |
| PUT | `/playlists/{id}` | Update playlist | Playlist object | Updated playlist |
| DELETE | `/playlists/{id}` | Delete playlist | - | 204 No Content |

**Playlist Object:**
```json
{
  "userId": "string",
  "title": "string",
  "description": "string"
}
```

### 4. Playlist Songs Management
**Base Path:** `/api/playlist-songs`

| Method | Endpoint | Description | Request Body | Response |
|--------|----------|-------------|--------------|----------|
| GET | `/playlist-songs/{playlistId}` | Get songs in playlist | - | List of playlist songs |
| POST | `/playlist-songs` | Add song to playlist | PlaylistSong object | Created playlist song |
| DELETE | `/playlist-songs/{id}` | Remove song from playlist | - | 204 No Content |

**PlaylistSong Object:**
```json
{
  "playlistId": 1,
  "songId": 2
}
```

### 5. Profile Management
**Base Path:** `/api/profiles`

| Method | Endpoint | Description | Request Body | Response |
|--------|----------|-------------|--------------|----------|
| GET | `/profiles` | Get all profiles | - | List of profiles |
| GET | `/profiles/{userId}` | Get profile by user ID | - | Profile object |
| POST | `/profiles` | Create new profile | Profile object | Created profile |
| PUT | `/profiles/{id}` | Update profile | Profile object | Updated profile |
| DELETE | `/profiles/{id}` | Delete profile | - | 204 No Content |

**Profile Object:**
```json
{
  "userId": "string",
  "name": "string",
  "email": "string",
  "preferences": {}
}
```

### 6. Song Management
**Base Path:** `/api/songs`

| Method | Endpoint | Description | Request Body | Response |
|--------|----------|-------------|--------------|----------|
| GET | `/songs` | Get all songs | - | List of songs |
| GET | `/songs/{id}` | Get song by ID | - | Song object |
| POST | `/songs` | Create new song | Song object | Created song |
| PUT | `/songs/{id}` | Update song | Song object | Updated song |
| DELETE | `/songs/{id}` | Delete song | - | 204 No Content |

**Song Object:**
```json
{
  "title": "string",
  "artist": "string",
  "album": "string",
  "features": {}
}
```

## Error Responses

All endpoints return standard HTTP status codes:

- `200 OK` - Success
- `201 Created` - Resource created successfully
- `204 No Content` - Success with no content
- `400 Bad Request` - Invalid request data
- `404 Not Found` - Resource not found
- `500 Internal Server Error` - Server error

Error response format:
```json
{
  "timestamp": "2025-01-12T10:30:00.000+00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Error description",
  "path": "/api/endpoint"
}
```

## Database Schema

The application uses PostgreSQL with the following main tables:
- `profiles` - User profiles
- `emotions` - Emotion tracking data
- `songs` - Music tracks
- `playlists` - User playlists
- `playlist_songs` - Playlist-song relationships

## Spotify Integration

The system integrates with Spotify's Web API for music recommendations:
- Uses Client Credentials flow for authentication
- Searches for tracks by genre based on emotion
- Returns formatted track information with Spotify URLs

## CORS Configuration

CORS is enabled for all origins to allow frontend integration during development.

## Development Setup

1. **Prerequisites:**
   - Java 17+
   - PostgreSQL 12+
   - Maven 3.6+

2. **Database Setup:**
   ```sql
   CREATE DATABASE emovibes;
   ```

3. **Configuration:**
   Update `application.properties` with your database credentials and Spotify API credentials.

4. **Running the Application:**
   ```bash
   cd backend/emotionmusic
   ./mvnw spring-boot:run
   ```

## Testing

Use the following test endpoints to verify functionality:

1. **Test Spotify Integration:**
   ```bash
   curl http://localhost:8080/api/recommend/happy
   curl http://localhost:8080/api/recommend/genres
   ```

2. **Test CRUD Operations:**
   ```bash
   # Create a profile
   curl -X POST http://localhost:8080/api/profiles \
     -H "Content-Type: application/json" \
     -d '{"userId":"test123","name":"Test User","email":"test@example.com"}'
   
   # Get all profiles
   curl http://localhost:8080/api/profiles
   ```

## Notes for Frontend Team

1. **CORS is enabled** - You can make requests from any frontend domain
2. **No authentication required** - All endpoints are open for development
3. **JSON responses** - All responses are in JSON format
4. **Error handling** - Check HTTP status codes and error response format
5. **Spotify integration** - Music recommendations come from Spotify API
6. **Database auto-creation** - Tables are created automatically on startup

## Future Enhancements

- User authentication and authorization
- Advanced emotion detection algorithms
- Machine learning-based recommendations
- Real-time emotion tracking
- Social features (sharing playlists)
