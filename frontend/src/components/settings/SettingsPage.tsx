import { useState } from 'react';
import ProfileSettings from './ProfileSettings';
import CurrencyManager from '../currency/CurrencyManager';

const TABS = ['Profile', 'Currencies'] as const;
type Tab = typeof TABS[number];

export default function SettingsPage() {
  const [activeTab, setActiveTab] = useState<Tab>('Profile');

  return (
    <div className="p-6">
      <h2 className="text-2xl font-bold mb-6">Settings</h2>

      <div className="border-b mb-6">
        <nav className="flex gap-6">
          {TABS.map((tab) => (
            <button
              key={tab}
              onClick={() => setActiveTab(tab)}
              className={`pb-2 text-sm font-medium ${
                activeTab === tab
                  ? 'border-b-2 border-blue-600 text-blue-600'
                  : 'text-gray-500 hover:text-gray-700'
              }`}
            >
              {tab}
            </button>
          ))}
        </nav>
      </div>

      {activeTab === 'Profile' && <ProfileSettings />}
      {activeTab === 'Currencies' && <CurrencyManager />}
    </div>
  );
}
