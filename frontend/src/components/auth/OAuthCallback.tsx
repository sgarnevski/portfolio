import { useEffect } from 'react';
import { useSearchParams, useNavigate } from 'react-router-dom';
import { useDispatch } from 'react-redux';
import { loginSuccess } from '../../store/slices/authSlice';

export default function OAuthCallback() {
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();
  const dispatch = useDispatch();

  useEffect(() => {
    const token = searchParams.get('token');
    const username = searchParams.get('username');
    const userId = searchParams.get('userId');

    if (token && username && userId) {
      localStorage.setItem('token', token);
      localStorage.setItem('user', JSON.stringify({ id: Number(userId), username }));
      dispatch(loginSuccess({ token, username, userId: Number(userId) }));
      navigate('/', { replace: true });
    } else {
      navigate('/login?error=oauth2', { replace: true });
    }
  }, [searchParams, navigate, dispatch]);

  return null;
}
