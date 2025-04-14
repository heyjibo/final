/**
 * Simplified mock data
 */

// Rider statistics data
export const riderStatistics = {
  ordersToday: 12,
  completionRate: 92,
  income: 183.5,
  totalIncome: 5400,
  distance: 28.5,
  rating: 4.8
}

// Pending orders
export const pendingOrders = [
  {
    id: 'O2023041601',
    address: 'Zhejiang University Yuquan Campus, Building 18',
    time: '15:30',
    amount: '$8.00',
    status: 'Pending'
  },
  {
    id: 'O2023041602',
    address: 'Huanglong Sports Center South Gate',
    time: '16:00',
    amount: '$12.00',
    status: 'Picking up'
  },
  {
    id: 'O2023041603',
    address: 'West Lake Cultural Square Metro Station',
    time: '16:15',
    amount: '$15.00',
    status: 'Pending'
  }
]

// Device list
export const deviceList = [
  {
    id: 1,
    deviceName: 'Rider Phone 1',
    imei: '123456789012345',
    sn: 'SN12345678',
    model: 'iPhone 13',
    status: 'Normal',
    bindTime: '2023-04-10 10:30:45'
  },
  {
    id: 2,
    deviceName: 'GPS Tracker',
    imei: '987654321098765',
    sn: 'SN98765432',
    model: 'GPS-T100',
    status: 'Normal',
    bindTime: '2023-03-15 09:20:30'
  },
  {
    id: 3,
    deviceName: 'Backup Device',
    imei: '555566667777888',
    sn: 'SN55667788',
    model: 'Xiaomi 12',
    status: 'Faulty',
    bindTime: '2023-02-20 14:45:10'
  }
]

// Delivery route data
export const routeData = {
  routes: [
    {
      id: 'R20230415001',
      startTime: '2023-04-15 09:30:00',
      endTime: '2023-04-15 10:15:00',
      distance: 5.2,
      status: 'Completed',
      orderCount: 3
    },
    {
      id: 'R20230415002',
      startTime: '2023-04-15 12:20:00',
      endTime: '2023-04-15 13:05:00',
      distance: 4.8,
      status: 'Completed', 
      orderCount: 2
    },
    {
      id: 'R20230415003',
      startTime: '2023-04-15 15:40:00',
      endTime: null,
      distance: 3.5,
      status: 'In Progress',
      orderCount: 2
    }
  ],
  // GeoJSON format route data example
  geoData: {
    type: 'FeatureCollection',
    features: [
      {
        type: 'Feature',
        geometry: {
          type: 'LineString',
          coordinates: [
            [120.15, 30.26],
            [120.16, 30.27],
            [120.17, 30.28],
            [120.18, 30.29]
          ]
        },
        properties: {
          routeId: 'R20230415003',
          timestamps: ['15:40:00', '15:50:00', '16:00:00', '16:10:00']
        }
      }
    ]
  }
}

// Event report data
export const eventReports = [
  {
    id: 'E20230415001',
    type: 'Traffic Accident',
    time: '2023-04-15 11:20:00',
    location: 'Xihu District, Wenyi West Road 969',
    description: 'Minor collision with an electric bike, no casualties',
    status: 'Processed',
    handler: 'System Admin',
    handleTime: '2023-04-15 11:45:00'
  },
  {
    id: 'E20230414001',
    type: 'Customer Complaint',
    time: '2023-04-14 13:30:00',
    location: 'Gongshu District, Moganshan Road 972',
    description: 'Customer reported food spilled, packaging insecure',
    status: 'Processed',
    handler: 'System Admin',
    handleTime: '2023-04-14 14:20:00'
  },
  {
    id: 'E20230413001',
    type: 'Device Malfunction',
    time: '2023-04-13 09:15:00',
    location: 'Binjiang District, Wangshang Road 699',
    description: 'GPS location abnormal, unable to navigate properly',
    status: 'Processing',
    handler: null,
    handleTime: null
  }
]

// Work order data
export const workOrders = [
  {
    id: 'W20230415001',
    title: 'Delivery Delay Handling',
    type: 'Delivery Issue',
    createTime: '2023-04-15 12:30:00',
    status: 'Pending',
    priority: 'Medium',
    description: 'Due to traffic control, multiple orders delayed over 30 minutes',
    assignee: 'Dispatch Center'
  },
  {
    id: 'W20230414001',
    title: 'Device Repair Request',
    type: 'Device Issue',
    createTime: '2023-04-14 10:45:00',
    status: 'Processing',
    priority: 'High',
    description: 'Phone screen damaged, affecting order handling',
    assignee: 'Technical Support'
  },
  {
    id: 'W20230413001',
    title: 'Order Cancellation Refund',
    type: 'Order Issue',
    createTime: '2023-04-13 16:20:00',
    status: 'Completed',
    priority: 'Low',
    description: 'Customer canceled order while delivery in progress, requesting compensation',
    assignee: 'Finance Department',
    resolveTime: '2023-04-13 17:30:00',
    solution: 'Compensated delivery fee of $15'
  }
] 