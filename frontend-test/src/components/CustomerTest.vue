<template>
  <div class="test-container">
    <h2>Customer Management</h2>
    
    <div class="form-section">
      <h3>Create/Update Customer</h3>
      <input v-model="form.customerid" placeholder="Customer ID">
      <div v-if="errors.customerid" class="error">{{ errors.customerid }}</div>
      
      <input v-model="form.name" placeholder="Name">
      <div v-if="errors.name" class="error">{{ errors.name }}</div>
      
      <input v-model="form.age" type="number" placeholder="Age">
      <div v-if="errors.age" class="error">{{ errors.age }}</div>
      
      <input v-model="form.address" placeholder="Location">
      <div v-if="errors.address" class="error">{{ errors.address }}</div>
      
      <input v-model="form.numOfOrder" type="number" placeholder="Order Quantity">
      <div v-if="errors.numOfOrder" class="error">{{ errors.numOfOrder }}</div>
      
      <input v-model="form.password" type="password" placeholder="Password">
      <div v-if="errors.password" class="error">{{ errors.password }}</div>
      
      <div class="button-group">
        <button @click="submitCustomer" class="submit-btn">Submit</button>
        <button @click="clearForm" class="clear-btn">Clear All</button>
         <button @click="clearAllCustomers" class="danger-btn">Clear All Customers</button>
      </div>
    </div>

    <div class="action-section">
      <button @click="fetchAllCustomers" class="fetch-btn">Get all customers</button>
    </div>

    <div class="result-section">
      <h3>Customer List</h3>
      <div v-if="customers.length === 0" class="empty-tip">No data available at the moment</div>
      <div v-for="c in customers" :key="c.customerid" class="customer-item">
        <div class="info">
          <span class="customer-id">ID: {{ c.customerid }}</span>
          <span>Name: {{ c.name }}</span>
          <span>Age: {{ c.age }}</span>
          <span>Orders: {{ c.numOfOrder }}</span>
        </div>
        <div class="actions">
          <button @click="editCustomer(c)" class="edit-btn">Edit</button>
          <button @click="deleteCustomer(c.customerid)" class="delete-btn">Delete</button>
        </div>
      </div>
    </div>

    <div v-if="responseData" class="response-message">
      <pre>{{ responseData }}</pre>
    </div>
  </div>
</template>

<script>
import axios from 'axios'

export default {
  data() {
    return {
      errors: { 
        customerid: '',
        name: '',
        age: '',
        address: '',
        numOfOrder: '',
        password: ''
      }, 
      form: {
        customerid: '',
        name: '',
        age: null,
        address: '',
        numOfOrder: null,
        password: ''
      },
      customers: [],
      responseData: null
    }
  },
  methods: {
    clearForm() {
      this.form = {
        customerid: '',
        name: '',
        age: null,
        address: '',
        numOfOrder: null,
        password: ''
      }
      this.responseData = null
    },
    async fetchAllCustomers() {
      try {
        const res = await axios.get('/api/customer')
        this.customers = res.data
      } catch (err) {
        alert('Failed to get message content: ' + err.response.data)
      }
    },
    async submitCustomer() {
      if (!this.validateCustomerForm()) return;
      try {
        const isUpdate = !!this.form.customerid;
        const method = isUpdate ? 'put' : 'post';
        const url = isUpdate 
          ? `/api/customer/${this.form.customerid}`
          : '/api/customer';

        const res = await axios[method](url, this.form);
        
        this.responseData = `${isUpdate ? 'Update' : 'Crate'}successful: ${JSON.stringify(res.data, null, 2)}`;
        this.fetchAllCustomers();
        if (!isUpdate) this.clearForm(); 
      } catch (err) {
        alert('Fail to operate: ' + err.response.data);
      }
    },
    editCustomer(customer) {
      this.form = { ...customer };
      this.responseData = null;
    },
    async clearAllCustomers() {
      if (!confirm('⚠️ Warning! This will delete all customer data, do you want to continue?')) return;
      
      try {
        await axios.delete('/api/customer/all');
        this.responseData = 'Successfully cleared all customer data!';
        this.fetchAllCustomers();
      } catch (err) {
        alert('Clearing failed: ' + err.response.data);
      }
    },

    async deleteCustomer(id) {
      if (confirm(`Are you sure to delete the customer ${id}?`)) {
        try {
          await axios.delete(`/api/customer/${id}`)
          this.fetchAllCustomers()
        } catch (err) {
          alert('Delete failed: ' + err.response.data)
        }
      }
    },
    validateCustomerForm() {
      let isValid = true;
      this.errors = {
        customerid: '',
        name: '',
        age: '',
        address: '',
        numOfOrder: '',
        password: ''
      };

      // CustomerID
      if (!this.form.customerid.trim()) {
        this.errors.customerid = 'Customer ID is required';
        isValid = false;
      }

      // Name
      if (!this.form.name.trim()) {
        this.errors.name = 'Name is required';
        isValid = false;
      } else if (this.form.name.trim().length < 2) {
        this.errors.name = 'Name must be at least 2 characters';
        isValid = false;
      }

      // Age
      if (!this.form.age && this.form.age !== 0) {
        this.errors.age = 'Age is required';
        isValid = false;
      } else if (this.form.age < 1 || this.form.age > 150) {
        this.errors.age = 'Age must be between 1-150';
        isValid = false;
      }

      // Address
      if (!this.form.address.trim()) {
        this.errors.address = 'Address is required';
        isValid = false;
      }

      // Order Quantity
      if (this.form.numOfOrder < 0) {
        this.errors.numOfOrder = 'Order quantity cannot be negative';
        isValid = false;
      }

      // Password
      if (!this.form.password) {
        this.errors.password = 'Password is required';
        isValid = false;
      } else if (this.form.password.length < 6) {
        this.errors.password = 'Password must be at least 6 characters';
        isValid = false;
      }

      return isValid;
    },
  }
}
</script>

<style scoped>
.test-container {
  max-width: 800px;
  margin: 2rem auto;
  padding: 1rem;
  border: 1px solid #e1e1e1;
  border-radius: 8px;
}

.form-section {
  margin-bottom: 2rem;
  padding: 1rem;
  background: #f8f9fa;
  border-radius: 6px;
}

input {
  display: block;
  width: 100%;
  padding: 0.5rem;
  margin: 0.5rem 0;
  border: 1px solid #ddd;
  border-radius: 4px;
}

.button-group {
  margin-top: 1rem;
  display: flex;
  gap: 0.5rem;
}

button {
  padding: 0.5rem 1rem;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  transition: opacity 0.3s;
}

button:hover {
  opacity: 0.8;
}

.submit-btn { background: #67C23A; color: white; }
.clear-btn { background: #909399; color: white; }
.fetch-btn { background: #409EFF; color: white; }
.delete-btn { background: #F56C6C; color: white; }

.customer-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0.8rem;
  margin: 0.5rem 0;
  border: 1px solid #eee;
  border-radius: 4px;
}

.info {
  flex: 1;
  display: flex;
  gap: 1rem;
  flex-wrap: wrap;
}

.customer-id {
  font-weight: bold;
  min-width: 120px;
}

.actions {
  display: flex;
  gap: 0.5rem;
}

.response-message {
  margin-top: 1rem;
  padding: 1rem;
  background: #f4f4f5;
  border-radius: 4px;
}

.empty-tip {
  color: #909399;
  text-align: center;
  padding: 1rem;
}

.error {
  color: #f56c6c;
  font-size: 12px;
  margin: -5px 0 10px 0;
}

.clear-btn { 
  background: #909399; 
  color: white; 
}
.edit-btn { 
  background: #E6A23C; 
  color: white; 
}
</style>