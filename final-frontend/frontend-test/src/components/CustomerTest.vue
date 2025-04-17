<template>
   <div class="test-container">
    <h2>Customer management testing</h2>
    
    <div class="form-section">
      <h3>Create/Update Customer</h3>
      <input v-model="form.customerid" placeholder="Customer ID">
      <div class="error">{{ errors.customerid }}</div>
      
      <input v-model="form.name" placeholder="Name">
      <div class="error">{{ errors.name }}</div>
      
      <input v-model="form.age" type="number" placeholder="Age">
      <div class="error">{{ errors.age }}</div>
      
      <input v-model="form.address" placeholder="Location">
      <div class="error">{{ errors.address }}</div>
      
      <input v-model="form.numOfOrder" type="number" placeholder="Order Quantity">
      <div class="error">{{ errors.numOfOrder }}</div>
      
      <input v-model="form.password" type="password" placeholder="Password">
      <div class="error">{{ errors.password }}</div>
      
      <button @click="submitCustomer">Submit</button>
    </div>
  
    

    <div class="action-buttons">
      <button @click="fetchAllCustomers">Get all customers</button>
    </div>

    
    <div class="result-section">
      <h3>Customer List</h3>
      <div v-for="c in customers" :key="c.customerid" class="customer-item">
        <p>ID: {{ c.customerid }} - Full Name: {{ c.name }}</p>
        <button @click="deleteCustomer(c.customerid)">Delete</button>
      </div>
      <pre v-if="responseData">{{ responseData }}</pre>
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
    async fetchAllCustomers() {
      try {
        const res = await axios.get('/api/customer')
        this.customers = res.data
      } catch (err) {
        alert('Failed to get message content: ' + err.response.data)
      }
    },
    async submitCustomer() {
      if (!this.validateCustomerForm()) return; // use valid
      try {
        const isUpdate = !!this.form.customerid;
        const method = isUpdate ? 'put' : 'post';
        const url = isUpdate 
          ? `/api/customer/${this.form.customerid}` 
          : '/api/customer'; 

        const res = await axios[method](url, this.form);
        
        this.responseData = res.data
        this.fetchAllCustomers()
      } catch (err) {
        alert('Failed to operate: ' + err.response.data)
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
    }
  }
}
</script>

<style scoped>
.error { 
  color: #f56c6c;
  font-size: 12px;
  margin: -5px 0 10px 0;
} 

</style>