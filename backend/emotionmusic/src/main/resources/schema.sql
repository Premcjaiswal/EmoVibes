-- Idempotent migration: non-destructive. Safe to run multiple times.
BEGIN;

-- 1) Profiles
CREATE TABLE IF NOT EXISTS public.profiles (
  id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
  user_id uuid NOT NULL UNIQUE REFERENCES auth.users(id) ON DELETE CASCADE,
  name text,
  email text UNIQUE,
  preferences jsonb DEFAULT '{}'::jsonb,
  created_at timestamp with time zone DEFAULT now(),
  updated_at timestamp with time zone DEFAULT now()
);
CREATE INDEX IF NOT EXISTS idx_profiles_user_id ON public.profiles(user_id);

-- 2) Emotions
CREATE TABLE IF NOT EXISTS public.emotions (
  id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
  user_id uuid NOT NULL REFERENCES auth.users(id) ON DELETE CASCADE,
  emotion_type text NOT NULL,
  confidence_score numeric(3,2) NOT NULL CHECK (confidence_score >= 0 AND confidence_score <= 1),
  input_type text NOT NULL CHECK (input_type IN ('camera','text','audio','sensor','other')),
  meta jsonb DEFAULT '{}'::jsonb,
  created_at timestamp with time zone DEFAULT now(),
  updated_at timestamp with time zone DEFAULT now()
);
CREATE INDEX IF NOT EXISTS idx_emotions_user_id ON public.emotions(user_id);
CREATE INDEX IF NOT EXISTS idx_emotions_created_at ON public.emotions(created_at);
CREATE INDEX IF NOT EXISTS idx_emotions_emotion_type ON public.emotions(emotion_type);
-- Use standard GIN; jsonb_path_ops is more limited — keep as originally requested if desired
DO $$ BEGIN
  IF NOT EXISTS (SELECT 1 FROM pg_class c JOIN pg_namespace n ON n.oid = c.relnamespace WHERE c.relkind = 'i' AND c.relname = 'idx_emotions_meta_gin') THEN
    CREATE INDEX idx_emotions_meta_gin ON public.emotions USING gin (meta jsonb_path_ops);
  END IF;
END$$;

-- 3) Songs
CREATE TABLE IF NOT EXISTS public.songs (
  id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
  title text NOT NULL,
  artist text,
  album text,
  genre text,
  duration_seconds integer,
  mood_tags jsonb DEFAULT '[]'::jsonb,
  external_source text,
  external_id text,
  external_unique text GENERATED ALWAYS AS (coalesce(external_source, '') || '::' || coalesce(external_id, '')) STORED,
  metadata jsonb DEFAULT '{}'::jsonb,
  created_at timestamp with time zone DEFAULT now(),
  updated_at timestamp with time zone DEFAULT now()
);

-- Ensure columns exist if table pre-exists (non-destructive)
ALTER TABLE public.songs ADD COLUMN IF NOT EXISTS mood_tags jsonb DEFAULT '[]'::jsonb;
ALTER TABLE public.songs ADD COLUMN IF NOT EXISTS external_source text;
ALTER TABLE public.songs ADD COLUMN IF NOT EXISTS external_id text;
-- Add generated column only if not present (Postgres won't allow ADD COLUMN ... GENERATED IF NOT EXISTS, so guard via checking pg_attribute)
DO $$
BEGIN
  IF NOT EXISTS (
    SELECT 1 FROM pg_attribute a
    JOIN pg_class c ON a.attrelid = c.oid
    JOIN pg_namespace n ON c.relnamespace = n.oid
    WHERE n.nspname = 'public' AND c.relname = 'songs' AND a.attname = 'external_unique'
  ) THEN
    ALTER TABLE public.songs ADD COLUMN external_unique text GENERATED ALWAYS AS (coalesce(external_source, '') || '::' || coalesce(external_id, '')) STORED;
  END IF;
END$$;
ALTER TABLE public.songs ADD COLUMN IF NOT EXISTS metadata jsonb DEFAULT '{}'::jsonb;
ALTER TABLE public.songs ADD COLUMN IF NOT EXISTS created_at timestamp with time zone DEFAULT now();
ALTER TABLE public.songs ADD COLUMN IF NOT EXISTS updated_at timestamp with time zone DEFAULT now();

-- Indexes for performance
CREATE INDEX IF NOT EXISTS idx_songs_artist ON public.songs(lower(artist));
CREATE INDEX IF NOT EXISTS idx_songs_genre ON public.songs(lower(genre));
CREATE INDEX IF NOT EXISTS idx_songs_title ON public.songs(lower(title));
CREATE INDEX IF NOT EXISTS idx_songs_external_unique ON public.songs(external_source, external_id);
CREATE INDEX IF NOT EXISTS idx_songs_mood_tags_gin ON public.songs USING gin (mood_tags);
CREATE INDEX IF NOT EXISTS idx_songs_metadata_gin ON public.songs USING gin (metadata);

-- 4) Playlists
CREATE TABLE IF NOT EXISTS public.playlists (
  id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
  user_id uuid NOT NULL REFERENCES auth.users(id) ON DELETE CASCADE,
  name text NOT NULL,
  description text,
  is_auto_generated boolean NOT NULL DEFAULT false,
  created_at timestamp with time zone DEFAULT now(),
  updated_at timestamp with time zone DEFAULT now()
);
CREATE INDEX IF NOT EXISTS idx_playlists_user_id ON public.playlists(user_id);
CREATE INDEX IF NOT EXISTS idx_playlists_is_auto_generated ON public.playlists(is_auto_generated);

-- 5) PlaylistSong
CREATE TABLE IF NOT EXISTS public.playlist_songs (
  id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
  playlist_id bigint NOT NULL REFERENCES public.playlists(id) ON DELETE CASCADE,
  song_id bigint NOT NULL REFERENCES public.songs(id) ON DELETE CASCADE,
  position integer NOT NULL,
  added_at timestamp with time zone DEFAULT now()
);
DO $$ BEGIN
  IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'playlist_song_unique_pos_per_playlist') THEN
    ALTER TABLE public.playlist_songs ADD CONSTRAINT playlist_song_unique_pos_per_playlist UNIQUE (playlist_id, position);
  END IF;
  IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'playlist_song_unique_song_per_playlist') THEN
    ALTER TABLE public.playlist_songs ADD CONSTRAINT playlist_song_unique_song_per_playlist UNIQUE (playlist_id, song_id);
  END IF;
END$$;
CREATE INDEX IF NOT EXISTS idx_playlist_songs_playlist_id ON public.playlist_songs(playlist_id);
CREATE INDEX IF NOT EXISTS idx_playlist_songs_song_id ON public.playlist_songs(song_id);

-- 6) UserPreference
CREATE TABLE IF NOT EXISTS public.user_preferences (
  id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
  user_id uuid NOT NULL UNIQUE REFERENCES auth.users(id) ON DELETE CASCADE,
  favorite_genres jsonb DEFAULT '[]'::jsonb,
  disliked_artists jsonb DEFAULT '[]'::jsonb,
  mood_weights jsonb DEFAULT '{}'::jsonb,
  metadata jsonb DEFAULT '{}'::jsonb,
  created_at timestamp with time zone DEFAULT now(),
  updated_at timestamp with time zone DEFAULT now()
);
CREATE INDEX IF NOT EXISTS idx_user_preferences_fav_genres_gin ON public.user_preferences USING gin (favorite_genres);
CREATE INDEX IF NOT EXISTS idx_user_preferences_disliked_artists_gin ON public.user_preferences USING gin (disliked_artists);
CREATE INDEX IF NOT EXISTS idx_user_preferences_mood_weights_gin ON public.user_preferences USING gin (mood_weights);

-- 7) Recommendation events
CREATE TABLE IF NOT EXISTS public.recommendation_events (
  id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
  user_id uuid REFERENCES auth.users(id) ON DELETE SET NULL,
  playlist_id bigint REFERENCES public.playlists(id) ON DELETE SET NULL,
  song_id bigint REFERENCES public.songs(id) ON DELETE SET NULL,
  event_type text NOT NULL,
  context jsonb DEFAULT '{}'::jsonb,
  created_at timestamp with time zone DEFAULT now()
);
CREATE INDEX IF NOT EXISTS idx_recommendation_events_user_id ON public.recommendation_events(user_id);
CREATE INDEX IF NOT EXISTS idx_recommendation_events_event_type ON public.recommendation_events(event_type);
CREATE INDEX IF NOT EXISTS idx_recommendation_events_created_at ON public.recommendation_events(created_at);
CREATE INDEX IF NOT EXISTS idx_recommendation_events_context_gin ON public.recommendation_events USING gin (context);

-- 8) Add UNIQUE constraint on songs.external_source + external_id only if it doesn't exist and there are no duplicates
DO $$
BEGIN
  IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'songs_external_unique') THEN
    IF NOT EXISTS (
      SELECT 1 FROM (
        SELECT external_source, external_id, count(*) c FROM public.songs GROUP BY 1,2 HAVING count(*) > 1
      ) t
    ) THEN
      ALTER TABLE public.songs ADD CONSTRAINT songs_external_unique UNIQUE (external_source, external_id);
    END IF;
  END IF;
END$$;

-- 9) Enable RLS on tables (safe; does not remove existing policies)
ALTER TABLE IF EXISTS public.profiles ENABLE ROW LEVEL SECURITY;
ALTER TABLE IF EXISTS public.emotions ENABLE ROW LEVEL SECURITY;
ALTER TABLE IF EXISTS public.songs ENABLE ROW LEVEL SECURITY;
ALTER TABLE IF EXISTS public.playlists ENABLE ROW LEVEL SECURITY;
ALTER TABLE IF EXISTS public.playlist_songs ENABLE ROW LEVEL SECURITY;
ALTER TABLE IF EXISTS public.user_preferences ENABLE ROW LEVEL SECURITY;
ALTER TABLE IF EXISTS public.recommendation_events ENABLE ROW LEVEL SECURITY;

COMMIT;