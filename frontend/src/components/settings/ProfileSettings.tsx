import { useEffect, useState } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { RootState } from '../../store';
import {
  fetchProfileRequest,
  updateProfileRequest,
  changePasswordRequest,
  clearUserError,
} from '../../store/slices/userSlice';
import LoadingSpinner from '../common/LoadingSpinner';
import ErrorAlert from '../common/ErrorAlert';

export default function ProfileSettings() {
  const dispatch = useDispatch();
  const { profile, loading, error, passwordChangeSuccess } = useSelector((state: RootState) => state.user);

  const [username, setUsername] = useState('');
  const [email, setEmail] = useState('');
  const [profileSaved, setProfileSaved] = useState(false);

  const [currentPassword, setCurrentPassword] = useState('');
  const [newPassword, setNewPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [passwordError, setPasswordError] = useState('');

  useEffect(() => {
    dispatch(fetchProfileRequest());
    return () => { dispatch(clearUserError()); };
  }, [dispatch]);

  useEffect(() => {
    if (profile) {
      setUsername(profile.username);
      setEmail(profile.email);
    }
  }, [profile]);

  const handleProfileSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    setProfileSaved(false);
    dispatch(updateProfileRequest({ username: username.trim(), email: email.trim() }));
    setProfileSaved(true);
  };

  const handlePasswordSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    setPasswordError('');
    if (newPassword !== confirmPassword) {
      setPasswordError('Passwords do not match');
      return;
    }
    dispatch(changePasswordRequest({ currentPassword, newPassword }));
    setCurrentPassword('');
    setNewPassword('');
    setConfirmPassword('');
  };

  if (loading && !profile) return <LoadingSpinner />;

  return (
    <div className="space-y-8">
      {error && <ErrorAlert message={error} />}

      <div className="bg-white rounded-lg shadow-sm border p-6">
        <h3 className="text-lg font-semibold mb-4">Profile Details</h3>
        <form onSubmit={handleProfileSubmit} className="space-y-4 max-w-md">
          <div>
            <label className="block text-sm font-medium text-gray-700">Username</label>
            <input
              value={username}
              onChange={(e) => { setUsername(e.target.value); setProfileSaved(false); }}
              className="mt-1 block w-full rounded-md border border-gray-300 px-3 py-2 focus:border-blue-500 focus:outline-none"
            />
          </div>
          <div>
            <label className="block text-sm font-medium text-gray-700">Email</label>
            <input
              type="email"
              value={email}
              onChange={(e) => { setEmail(e.target.value); setProfileSaved(false); }}
              className="mt-1 block w-full rounded-md border border-gray-300 px-3 py-2 focus:border-blue-500 focus:outline-none"
            />
          </div>
          {profile && (
            <p className="text-xs text-gray-400">Member since {new Date(profile.createdAt).toLocaleDateString()}</p>
          )}
          <div className="flex items-center gap-3">
            <button type="submit" disabled={loading} className="bg-blue-600 text-white py-2 px-4 rounded-md hover:bg-blue-700 text-sm disabled:opacity-50">
              Save
            </button>
            {profileSaved && !error && !loading && (
              <span className="text-green-600 text-sm">Saved</span>
            )}
          </div>
        </form>
      </div>

      <div className="bg-white rounded-lg shadow-sm border p-6">
        <h3 className="text-lg font-semibold mb-4">Change Password</h3>
        <form onSubmit={handlePasswordSubmit} className="space-y-4 max-w-md">
          <div>
            <label className="block text-sm font-medium text-gray-700">Current Password</label>
            <input
              type="password"
              value={currentPassword}
              onChange={(e) => setCurrentPassword(e.target.value)}
              className="mt-1 block w-full rounded-md border border-gray-300 px-3 py-2 focus:border-blue-500 focus:outline-none"
            />
          </div>
          <div>
            <label className="block text-sm font-medium text-gray-700">New Password</label>
            <input
              type="password"
              value={newPassword}
              onChange={(e) => setNewPassword(e.target.value)}
              className="mt-1 block w-full rounded-md border border-gray-300 px-3 py-2 focus:border-blue-500 focus:outline-none"
            />
          </div>
          <div>
            <label className="block text-sm font-medium text-gray-700">Confirm New Password</label>
            <input
              type="password"
              value={confirmPassword}
              onChange={(e) => setConfirmPassword(e.target.value)}
              className="mt-1 block w-full rounded-md border border-gray-300 px-3 py-2 focus:border-blue-500 focus:outline-none"
            />
          </div>
          {passwordError && <p className="text-red-500 text-sm">{passwordError}</p>}
          <div className="flex items-center gap-3">
            <button type="submit" disabled={loading} className="bg-blue-600 text-white py-2 px-4 rounded-md hover:bg-blue-700 text-sm disabled:opacity-50">
              Change Password
            </button>
            {passwordChangeSuccess && (
              <span className="text-green-600 text-sm">Password changed</span>
            )}
          </div>
        </form>
      </div>
    </div>
  );
}
