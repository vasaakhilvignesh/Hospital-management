import React, { createContext, useContext, useState, useEffect } from 'react';
import { User } from '../types';
import { authService, patientService, doctorService } from '../services/modules';

interface AuthContextType {
  user: User | null;
  token: string | null;
  loading: boolean;
  login: (token: string, user: User) => Promise<void>;
  logout: () => void;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const AuthProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [user, setUser] = useState<User | null>(() => {
    const saved = localStorage.getItem('user');
    return saved ? JSON.parse(saved) : null;
  });
  const [token, setToken] = useState<string | null>(() => localStorage.getItem('token'));
  const [loading, setLoading] = useState(true);

  const enrichProfile = async (profile: User): Promise<User> => {
    const enriched = { ...profile };
    try {
      if (profile.role === 'PATIENT') {
        const pat = await patientService.getMe();
        if (pat && pat.id) {
          enriched.patientId = pat.id;
        }
      } else if (profile.role === 'DOCTOR') {
        const doc = await doctorService.getMe();
        if (doc && doc.id) {
          enriched.doctorId = doc.id;
        }
      }
    } catch (e) {
      // Fallback if not yet created or error
    }
    return enriched;
  };

  useEffect(() => {
    const initAuth = async () => {
      const savedToken = localStorage.getItem('token');
      if (savedToken) {
        try {
          const profile = await authService.getMe();
          const enriched = await enrichProfile(profile);
          setUser(enriched);
          localStorage.setItem('user', JSON.stringify(enriched));
        } catch {
          logout();
        }
      }
      setLoading(false);
    };
    initAuth();
  }, []);

  const login = async (newToken: string, newUser: User) => {
    localStorage.setItem('token', newToken);
    const enriched = await enrichProfile(newUser);
    localStorage.setItem('user', JSON.stringify(enriched));
    setToken(newToken);
    setUser(enriched);
  };

  const logout = () => {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    setToken(null);
    setUser(null);
  };

  return (
    <AuthContext.Provider value={{ user, token, loading, login, logout }}>
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};
